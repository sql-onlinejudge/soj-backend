-- Problem 13~22: 추가 문제
INSERT INTO problems (name, description, schema_sql, difficulty, time_limit, is_order_sensitive) VALUES
(
    '특정 컬럼만 조회하기',
    '**[문제 설명]**\n\n회원 테이블에서 이름과 나이만 조회하려고 합니다.\n필요한 컬럼만 선택하여 출력하세요.\n\n**[테이블 구조]**\n- `users`: 회원 정보 테이블\n  - `id`: 회원 ID (INT)\n  - `name`: 회원 이름 (VARCHAR)\n  - `age`: 나이 (INT)\n\n**[출력 형식]**\nname, age 순서로 출력합니다.',
    'CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100), age INT)',
    1, 5000, false
),
(
    '중복 제거하여 조회',
    '**[문제 설명]**\n\n상품 테이블에서 중복 없이 카테고리 목록만 조회하려고 합니다.\nDISTINCT를 사용하여 중복을 제거하세요.\n\n**[테이블 구조]**\n- `products`: 상품 정보\n  - `id`: 상품 ID\n  - `name`: 상품명\n  - `category`: 카테고리\n\n**[출력 형식]**\ncategory만 출력 (중복 제거)',
    'CREATE TABLE products (id INT PRIMARY KEY, name VARCHAR(100), category VARCHAR(50))',
    1, 5000, false
),
(
    '이름으로 검색하기',
    '**[문제 설명]**\n\n회원 중 이름에 "김"이 포함된 회원을 찾으려고 합니다.\nLIKE 연산자를 사용하여 패턴 매칭하세요.\n\n**[테이블 구조]**\n- `users`: 회원 정보 (id, name, age)\n\n**[조건]**\n- name에 "김"이 포함된 회원\n\n**[힌트]**\nLIKE ''%김%'' 형태로 사용합니다.',
    'CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100), age INT)',
    2, 5000, false
),
(
    '나이 범위 조회',
    '**[문제 설명]**\n\n20세 이상 30세 이하인 회원을 조회하려고 합니다.\nBETWEEN을 사용하여 범위 조건을 적용하세요.\n\n**[테이블 구조]**\n- `users`: 회원 정보 (id, name, age)\n\n**[조건]**\n- age가 20 이상 30 이하\n\n**[힌트]**\nBETWEEN 20 AND 30을 사용합니다.',
    'CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100), age INT)',
    2, 5000, false
),
(
    '여러 조건으로 필터링',
    '**[문제 설명]**\n\n특정 부서에 속한 직원만 조회하려고 합니다.\nIN 연산자를 사용하여 영업부 또는 개발부 직원을 찾으세요.\n\n**[테이블 구조]**\n- `employees`: 직원 정보\n  - `id`: 직원 ID\n  - `name`: 이름\n  - `dept`: 부서명\n\n**[조건]**\n- dept가 영업 또는 개발인 직원',
    'CREATE TABLE employees (id INT PRIMARY KEY, name VARCHAR(100), dept VARCHAR(50))',
    3, 5000, false
),
(
    '조건부 값 출력',
    '**[문제 설명]**\n\n회원의 나이에 따라 등급을 표시하려고 합니다.\nCASE WHEN을 사용하여 조건부 값을 출력하세요.\n\n**[테이블 구조]**\n- `users`: 회원 정보 (id, name, age)\n\n**[출력 형식]**\nname, 등급 순서로 출력\n- 30세 이상: VIP\n- 20세 이상: 일반\n- 그 외: 주니어',
    'CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100), age INT)',
    3, 5000, false
),
(
    'NULL 처리하기',
    '**[문제 설명]**\n\n회원의 전화번호가 NULL인 경우 "미등록"으로 표시하려고 합니다.\nCOALESCE 또는 IFNULL을 사용하세요.\n\n**[테이블 구조]**\n- `users`: 회원 정보\n  - `id`: 회원 ID\n  - `name`: 이름\n  - `phone`: 전화번호 (NULL 가능)\n\n**[출력 형식]**\nname, 전화번호(NULL이면 미등록) 순서로 출력',
    'CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100), phone VARCHAR(20))',
    3, 5000, false
),
(
    '두 테이블 합치기',
    '**[문제 설명]**\n\n현재 직원과 퇴사 직원 목록을 합쳐서 조회하려고 합니다.\nUNION을 사용하여 두 테이블의 결과를 합치세요.\n\n**[테이블 구조]**\n- `employees`: 현재 직원 (id, name)\n- `former_employees`: 퇴사 직원 (id, name)\n\n**[출력 형식]**\nname만 출력 (중복 제거)',
    'CREATE TABLE employees (id INT PRIMARY KEY, name VARCHAR(100)); CREATE TABLE former_employees (id INT PRIMARY KEY, name VARCHAR(100))',
    4, 5000, false
),
(
    '주문 있는 상품만',
    '**[문제 설명]**\n\n한 번이라도 주문된 적이 있는 상품만 조회하려고 합니다.\nEXISTS를 사용하여 주문 이력이 있는 상품을 찾으세요.\n\n**[테이블 구조]**\n- `products`: 상품 정보 (id, name)\n- `order_items`: 주문 상품 (id, product_id, quantity)\n\n**[출력 형식]**\n상품 id, name 순서로 출력',
    'CREATE TABLE products (id INT PRIMARY KEY, name VARCHAR(100)); CREATE TABLE order_items (id INT PRIMARY KEY, product_id INT, quantity INT)',
    4, 5000, false
),
(
    '누적 합계 계산',
    '**[문제 설명]**\n\n일별 매출과 누적 매출을 함께 조회하려고 합니다.\n윈도우 함수 SUM() OVER를 사용하여 누적 합계를 계산하세요.\n\n**[테이블 구조]**\n- `sales`: 매출 정보\n  - `id`: ID\n  - `sale_date`: 판매일\n  - `amount`: 매출액\n\n**[출력 형식]**\nsale_date, amount, 누적합계 순서로 출력\n날짜 오름차순 정렬',
    'CREATE TABLE sales (id INT PRIMARY KEY, sale_date DATE, amount INT)',
    5, 5000, true
);

-- Test Cases for Problem 13: 특정 컬럼만 조회하기
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(13, 'INSERT INTO users VALUES (1, ''김철수'', 25), (2, ''이영희'', 30)', '김철수\t25\n이영희\t30'),
(13, 'INSERT INTO users VALUES (1, ''홍길동'', 28), (2, ''김영수'', 22), (3, ''박민수'', 35)', '홍길동\t28\n김영수\t22\n박민수\t35');

-- Test Cases for Problem 14: 중복 제거하여 조회
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(14, 'INSERT INTO products VALUES (1, ''사과'', ''과일''), (2, ''바나나'', ''과일''), (3, ''당근'', ''채소'')', '과일\n채소'),
(14, 'INSERT INTO products VALUES (1, ''셔츠'', ''의류''), (2, ''바지'', ''의류''), (3, ''노트북'', ''전자'')', '의류\n전자');

-- Test Cases for Problem 15: 이름으로 검색하기
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(15, 'INSERT INTO users VALUES (1, ''김철수'', 25), (2, ''이영희'', 30), (3, ''김영수'', 28)', '1\t김철수\t25\n3\t김영수\t28'),
(15, 'INSERT INTO users VALUES (1, ''박김수'', 22), (2, ''홍길동'', 30)', '1\t박김수\t22');

-- Test Cases for Problem 16: 나이 범위 조회
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(16, 'INSERT INTO users VALUES (1, ''A'', 19), (2, ''B'', 20), (3, ''C'', 25), (4, ''D'', 30), (5, ''E'', 31)', '2\tB\t20\n3\tC\t25\n4\tD\t30'),
(16, 'INSERT INTO users VALUES (1, ''김철수'', 25), (2, ''이영희'', 35)', '1\t김철수\t25');

-- Test Cases for Problem 17: 여러 조건으로 필터링
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(17, 'INSERT INTO employees VALUES (1, ''김철수'', ''영업''), (2, ''이영희'', ''인사''), (3, ''박민수'', ''개발'')', '1\t김철수\t영업\n3\t박민수\t개발'),
(17, 'INSERT INTO employees VALUES (1, ''A'', ''개발''), (2, ''B'', ''개발''), (3, ''C'', ''총무'')', '1\tA\t개발\n2\tB\t개발');

-- Test Cases for Problem 18: 조건부 값 출력
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(18, 'INSERT INTO users VALUES (1, ''김철수'', 35), (2, ''이영희'', 25), (3, ''박민수'', 18)', '김철수\tVIP\n이영희\t일반\n박민수\t주니어'),
(18, 'INSERT INTO users VALUES (1, ''A'', 30), (2, ''B'', 20), (3, ''C'', 19)', 'A\tVIP\nB\t일반\nC\t주니어');

-- Test Cases for Problem 19: NULL 처리하기
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(19, 'INSERT INTO users VALUES (1, ''김철수'', ''010-1234-5678''), (2, ''이영희'', NULL)', '김철수\t010-1234-5678\n이영희\t미등록'),
(19, 'INSERT INTO users VALUES (1, ''A'', NULL), (2, ''B'', NULL), (3, ''C'', ''010-0000-0000'')', 'A\t미등록\nB\t미등록\nC\t010-0000-0000');

-- Test Cases for Problem 20: 두 테이블 합치기
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(20, 'INSERT INTO employees VALUES (1, ''김철수''), (2, ''이영희''); INSERT INTO former_employees VALUES (1, ''박민수''), (2, ''홍길동'')', '김철수\n박민수\n이영희\n홍길동'),
(20, 'INSERT INTO employees VALUES (1, ''A''); INSERT INTO former_employees VALUES (1, ''A''), (2, ''B'')', 'A\nB');

-- Test Cases for Problem 21: 주문 있는 상품만
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(21, 'INSERT INTO products VALUES (1, ''사과''), (2, ''바나나''), (3, ''체리''); INSERT INTO order_items VALUES (1, 1, 5), (2, 3, 2)', '1\t사과\n3\t체리'),
(21, 'INSERT INTO products VALUES (1, ''A''), (2, ''B''); INSERT INTO order_items VALUES (1, 2, 10)', '2\tB');

-- Test Cases for Problem 22: 누적 합계 계산
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(22, 'INSERT INTO sales VALUES (1, ''2024-01-01'', 100), (2, ''2024-01-02'', 200), (3, ''2024-01-03'', 150)', '2024-01-01\t100\t100\n2024-01-02\t200\t300\n2024-01-03\t150\t450'),
(22, 'INSERT INTO sales VALUES (1, ''2024-01-01'', 50), (2, ''2024-01-02'', 50)', '2024-01-01\t50\t50\n2024-01-02\t50\t100');
