-- 테이블 생성 순서: 참조되는 테이블을 먼저 생성합니다.

-- 1. categories 테이블
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- 2. users 테이블 (회원 정보)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    -- 해시된 패스워드 저장을 위해 충분한 길이 확보 (예: VARCHAR(255))
    password VARCHAR(255) NOT NULL, 
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. organizations 테이블 (조직 정보)
CREATE TABLE organizations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    logo_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 외래 키 설정: user_id는 users.id를 참조
    FOREIGN KEY (user_id) REFERENCES users(id)
    -- ON DELETE RESTRICT (기본값)
);

-- 4. services 테이블 (창업자가 만드는 서비스)
CREATE TABLE services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    organization_id INT NOT NULL,
    name VARCHAR(255),
    category_id INT NOT NULL,
    description TEXT NOT NULL,
    cover_image_url VARCHAR(255),
    view INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 외래 키 설정
    FOREIGN KEY (organization_id) REFERENCES organizations(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 5. homepages 테이블 (AI Builder로 제작된 홈페이지)
CREATE TABLE homepages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    domain VARCHAR(255) UNIQUE NOT NULL,
    service_id INT NOT NULL,
    -- MySQL 8.0 이상에서 JSON 데이터 타입 사용
    config_json JSON, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 외래 키 설정
    FOREIGN KEY (service_id) REFERENCES services(id)
);

-- 6. subscribers 테이블 (대기 고객 이메일 목록)
-- email 필드의 UNIQUE 제약 조건은 유지하되, 필요에 따라 유연성을 위해 제거를 고려할 수 있습니다.
CREATE TABLE subscribers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT, -- NULL 허용 (비회원)
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 외래 키 설정
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 7. subscriptions 테이블 (서비스별 구독 정보)
CREATE TABLE subscriptions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subscriber_id INT NOT NULL,
    service_id INT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 복합 Unique Index 설정: 한 구독자가 한 서비스를 중복 구독할 수 없도록 보장
    UNIQUE KEY uk_subscriber_service (subscriber_id, service_id),
    -- 외래 키 설정
    FOREIGN KEY (subscriber_id) REFERENCES subscribers(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);