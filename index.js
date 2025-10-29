
require('dotenv').config();
const express = require('express');
const bodyParser = require('body-parser');
const mysql = require('mysql2/promise');

const app = express();
const port = 3000;

app.use(bodyParser.json());

// 데이터베이스 연결 풀 생성
// 중요: 아래 정보는 실제 데이터베이스 환경에 맞게 수정해야 합니다.
// .env 파일을 만들어 데이터베이스 정보를 관리하는 것을 권장합니다.
const pool = mysql.createPool({
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '[YOUR_PASSWORD]', // 여기에 실제 비밀번호를 입력하세요.
    database: process.env.DB_DATABASE || 'enstart_db',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

app.post('/subscribe-by-domain', async (req, res) => {
    const { email, domain } = req.body;

    if (!email || !domain) {
        return res.status(400).json({ message: 'Email and domain are required.' });
    }

    let connection;
    try {
        connection = await pool.getConnection();
        await connection.beginTransaction();

        // 1. 도메인으로 service_id 찾기
        const [homepages] = await connection.execute(
            'SELECT service_id FROM homepages WHERE domain = ?',
            [domain]
        );

        if (homepages.length === 0) {
            await connection.rollback();
            return res.status(404).json({ message: 'Domain not found.' });
        }
        const serviceId = homepages[0].service_id;

        // 2. 구독자(subscriber) 처리
        let subscriberId;
        const [subscribers] = await connection.execute(
            'SELECT id FROM subscribers WHERE email = ?',
            [email]
        );

        if (subscribers.length > 0) {
            subscriberId = subscribers[0].id;
        } else {
            const [result] = await connection.execute(
                'INSERT INTO subscribers (email) VALUES (?)',
                [email]
            );
            subscriberId = result.insertId;
        }

        // 3. 구독(subscription) 정보 생성
        try {
            await connection.execute(
                'INSERT INTO subscriptions (subscriber_id, service_id) VALUES (?, ?)',
                [subscriberId, serviceId]
            );
        } catch (error) {
            // UNIQUE KEY 제약 조건 위반 (이미 구독한 경우)
            if (error.code === 'ER_DUP_ENTRY') {
                await connection.rollback();
                return res.status(409).json({ message: 'You are already subscribed to this service.' });
            }
            throw error; // 다른 에러는 다시 던짐
        }

        await connection.commit();
        res.status(201).json({ message: 'Subscription successful.' });

    } catch (error) {
        if (connection) {
            await connection.rollback();
        }
        console.error('Error during subscription process:', error);
        res.status(500).json({ message: 'An error occurred during the subscription process.' });
    } finally {
        if (connection) {
            connection.release();
        }
    }
});

app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});
