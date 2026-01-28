-- Problems
INSERT INTO problems (name, description, schema_sql, difficulty, time_limit, is_order_sensitive) VALUES
(
    '회원 전체 목록 조회하기',
    '**[문제 설명]**\n\n온라인 쇼핑몰의 회원 관리 시스템을 개발하고 있습니다.\n회원 테이블에서 모든 회원 정보를 조회하는 쿼리를 작성해주세요.\n\n**[테이블 구조]**\n- `users`: 회원 정보 테이블\n  - `id`: 회원 ID (INT)\n  - `name`: 회원 이름 (VARCHAR)\n  - `age`: 나이 (INT)\n\n**[출력 형식]**\nid, name, age 순서로 모든 컬럼을 출력합니다.',
    'CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100), age INT)',
    1, 5000, false
),
(
    '성인 회원 필터링',
    '**[문제 설명]**\n\n성인 인증이 필요한 서비스를 위해 20세 이상인 회원만 조회해야 합니다.\nWHERE 절을 사용하여 조건에 맞는 회원을 필터링하세요.\n\n**[테이블 구조]**\n- `users`: 회원 정보 테이블\n  - `id`: 회원 ID\n  - `name`: 회원 이름\n  - `age`: 나이\n\n**[조건]**\n- age가 20 이상인 회원만 출력\n\n**[힌트]**\nWHERE 절에서 비교 연산자(>=)를 사용하세요.',
    'CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100), age INT)',
    1, 5000, false
),
(
    '회원 이름순 정렬',
    '**[문제 설명]**\n\n회원 목록을 이름 기준 알파벳 순서로 정렬하여 보여주려고 합니다.\nORDER BY를 사용하여 정렬된 결과를 출력하세요.\n\n**[테이블 구조]**\n- `users`: 회원 정보 테이블\n\n**[정렬 조건]**\n- name 기준 오름차순(ASC) 정렬\n\n**[주의]**\n정렬 순서가 정확해야 정답으로 인정됩니다.',
    'CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100), age INT)',
    2, 5000, true
),
(
    '고객별 총 주문금액 계산',
    '**[문제 설명]**\n\nVIP 고객을 선별하기 위해 각 고객별 총 주문금액을 계산해야 합니다.\nJOIN과 GROUP BY를 활용하여 고객별 주문 총액을 구하세요.\n\n**[테이블 구조]**\n- `users`: 고객 정보\n  - `id`: 고객 ID\n  - `name`: 고객 이름\n- `orders`: 주문 정보\n  - `id`: 주문 ID\n  - `user_id`: 고객 ID (FK)\n  - `amount`: 주문 금액\n\n**[출력 형식]**\nuser_id, name, 총주문금액 순서로 출력',
    'CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100)); CREATE TABLE orders (id INT PRIMARY KEY, user_id INT, amount INT)',
    2, 5000, false
),
(
    '최고가 상품 찾기',
    '**[문제 설명]**\n\n쇼핑몰에서 가장 비싼 상품을 메인 페이지에 노출하려고 합니다.\n가격이 가장 높은 상품을 조회하는 쿼리를 작성하세요.\n\n**[테이블 구조]**\n- `products`: 상품 정보\n  - `id`: 상품 ID\n  - `name`: 상품명\n  - `price`: 가격\n\n**[힌트]**\nMAX() 함수 또는 ORDER BY + LIMIT를 활용할 수 있습니다.',
    'CREATE TABLE products (id INT PRIMARY KEY, name VARCHAR(100), price INT)',
    2, 5000, false
),
(
    '카테고리별 상품 통계',
    '**[문제 설명]**\n\n상품 카테고리별 재고 현황을 파악하기 위해 카테고리별 상품 개수를 조회합니다.\nGROUP BY를 사용하여 집계하세요.\n\n**[테이블 구조]**\n- `products`: 상품 정보\n  - `id`: 상품 ID\n  - `name`: 상품명\n  - `category`: 카테고리\n\n**[출력 형식]**\ncategory, 상품개수 순서로 출력',
    'CREATE TABLE products (id INT PRIMARY KEY, name VARCHAR(100), category VARCHAR(50))',
    3, 5000, false
),
(
    '주문 내역 상세 조회',
    '**[문제 설명]**\n\n고객 서비스팀에서 주문 내역을 확인할 때 고객 정보와 주문 정보를 함께 볼 수 있어야 합니다.\nINNER JOIN을 사용하여 두 테이블을 연결하세요.\n\n**[테이블 구조]**\n- `users`: 고객 정보 (id, name)\n- `orders`: 주문 정보 (id, user_id, product)\n\n**[출력 형식]**\n모든 컬럼을 users, orders 순서로 출력',
    'CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100)); CREATE TABLE orders (id INT PRIMARY KEY, user_id INT, product VARCHAR(100))',
    3, 5000, false
),
(
    '평균 이상 고객 찾기',
    '**[문제 설명]**\n\n마케팅 타겟팅을 위해 평균 나이보다 많은 고객을 찾아야 합니다.\n서브쿼리를 활용하여 평균 나이를 먼저 계산하고, 그보다 나이가 많은 고객을 조회하세요.\n\n**[테이블 구조]**\n- `users`: 고객 정보 (id, name, age)\n\n**[힌트]**\nWHERE 절에서 서브쿼리로 AVG(age)를 계산하세요.',
    'CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100), age INT)',
    3, 5000, false
),
(
    '우수 고객 선별 (HAVING)',
    '**[문제 설명]**\n\n재구매율이 높은 우수 고객에게 쿠폰을 발송하려고 합니다.\n주문 횟수가 2회 이상인 고객의 user_id를 조회하세요.\n\n**[테이블 구조]**\n- `orders`: 주문 정보 (id, user_id, amount)\n\n**[출력 형식]**\nuser_id만 출력\n\n**[힌트]**\nGROUP BY와 HAVING 절을 함께 사용하세요.',
    'CREATE TABLE orders (id INT PRIMARY KEY, user_id INT, amount INT)',
    4, 5000, false
),
(
    '미주문 고객 찾기',
    '**[문제 설명]**\n\n회원가입 후 아직 주문을 하지 않은 고객에게 첫 구매 혜택을 안내하려고 합니다.\nLEFT JOIN과 NULL 체크를 활용하여 주문 이력이 없는 고객을 찾으세요.\n\n**[테이블 구조]**\n- `users`: 고객 정보 (id, name)\n- `orders`: 주문 정보 (id, user_id)\n\n**[출력 형식]**\nid, name 순서로 출력',
    'CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100)); CREATE TABLE orders (id INT PRIMARY KEY, user_id INT)',
    4, 5000, false
),
(
    '부서별 급여 순위 (윈도우 함수)',
    '**[문제 설명]**\n\n인사팀에서 부서별 급여 순위를 파악하려고 합니다.\n윈도우 함수 RANK() 또는 ROW_NUMBER()를 사용하여 각 부서 내에서의 급여 순위를 계산하세요.\n\n**[테이블 구조]**\n- `employees`: 직원 정보\n  - `id`: 직원 ID\n  - `name`: 이름\n  - `dept`: 부서명\n  - `salary`: 급여\n\n**[출력 형식]**\nname, dept, salary, rank 순서로 출력\n급여가 높을수록 순위가 높습니다 (1위가 최고).',
    'CREATE TABLE employees (id INT PRIMARY KEY, name VARCHAR(100), dept VARCHAR(50), salary INT)',
    5, 5000, true
),
(
    '조직도 탐색 (재귀 CTE)',
    '**[문제 설명]**\n\n조직도 시스템에서 특정 관리자의 모든 하위 직원을 조회해야 합니다.\n재귀 CTE(Common Table Expression)를 사용하여 manager_id가 NULL이 아닌 모든 직원(CEO 제외)을 계층적으로 조회하세요.\n\n**[테이블 구조]**\n- `employees`: 직원 정보\n  - `id`: 직원 ID\n  - `name`: 이름\n  - `manager_id`: 상위 관리자 ID (NULL이면 최상위)\n\n**[출력 형식]**\nid, name, manager_id 순서로 출력',
    'CREATE TABLE employees (id INT PRIMARY KEY, name VARCHAR(100), manager_id INT)',
    5, 5000, false
);

-- Test Cases for Problem 1: 회원 전체 목록 조회하기 (5개)
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(1, 'INSERT INTO users VALUES (1, ''김철수'', 25), (2, ''이영희'', 30), (3, ''박민수'', 22)', '1\t김철수\t25\n2\t이영희\t30\n3\t박민수\t22'),
(1, 'INSERT INTO users VALUES (1, ''홍길동'', 28)', '1\t홍길동\t28'),
(1, 'INSERT INTO users VALUES (1, ''Alice'', 20), (2, ''Bob'', 25), (3, ''Charlie'', 30), (4, ''Diana'', 35), (5, ''Eve'', 40)', '1\tAlice\t20\n2\tBob\t25\n3\tCharlie\t30\n4\tDiana\t35\n5\tEve\t40'),
(1, 'INSERT INTO users VALUES (100, ''테스트'', 99)', '100\t테스트\t99'),
(1, 'INSERT INTO users VALUES (1, ''A'', 1), (2, ''B'', 2)', '1\tA\t1\n2\tB\t2');

-- Test Cases for Problem 2: 성인 회원 필터링 (5개)
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(2, 'INSERT INTO users VALUES (1, ''김철수'', 25), (2, ''이영희'', 18), (3, ''박민수'', 30)', '1\t김철수\t25\n3\t박민수\t30'),
(2, 'INSERT INTO users VALUES (1, ''홍길동'', 19), (2, ''김영수'', 20)', '2\t김영수\t20'),
(2, 'INSERT INTO users VALUES (1, ''A'', 20), (2, ''B'', 21), (3, ''C'', 22)', '1\tA\t20\n2\tB\t21\n3\tC\t22'),
(2, 'INSERT INTO users VALUES (1, ''미성년자'', 15), (2, ''청소년'', 17), (3, ''성인'', 25)', '3\t성인\t25'),
(2, 'INSERT INTO users VALUES (1, ''경계값'', 20)', '1\t경계값\t20');

-- Test Cases for Problem 3: 회원 이름순 정렬 (5개)
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(3, 'INSERT INTO users VALUES (1, ''Charlie'', 25), (2, ''Alice'', 30), (3, ''Bob'', 22)', '2\tAlice\t30\n3\tBob\t22\n1\tCharlie\t25'),
(3, 'INSERT INTO users VALUES (1, ''Zoe'', 20), (2, ''Amy'', 25)', '2\tAmy\t25\n1\tZoe\t20'),
(3, 'INSERT INTO users VALUES (1, ''가'', 20), (2, ''나'', 25), (3, ''다'', 30)', '1\t가\t20\n2\t나\t25\n3\t다\t30'),
(3, 'INSERT INTO users VALUES (1, ''David'', 28), (2, ''Anna'', 32), (3, ''Brian'', 45), (4, ''Chris'', 23)', '2\tAnna\t32\n3\tBrian\t45\n4\tChris\t23\n1\tDavid\t28'),
(3, 'INSERT INTO users VALUES (1, ''Single'', 100)', '1\tSingle\t100');

-- Test Cases for Problem 4: 고객별 총 주문금액 계산
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(4, 'INSERT INTO users VALUES (1, ''김철수''), (2, ''이영희''); INSERT INTO orders VALUES (1, 1, 100), (2, 1, 200), (3, 2, 150)', '1\t김철수\t300\n2\t이영희\t150'),
(4, 'INSERT INTO users VALUES (1, ''홍길동''); INSERT INTO orders VALUES (1, 1, 50), (2, 1, 50), (3, 1, 50)', '1\t홍길동\t150');

-- Test Cases for Problem 5: 최고가 상품 찾기 (5개)
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(5, 'INSERT INTO products VALUES (1, ''사과'', 1000), (2, ''바나나'', 500), (3, ''체리'', 2000)', '3\t체리\t2000'),
(5, 'INSERT INTO products VALUES (1, ''스마트폰'', 500000), (2, ''노트북'', 1000000)', '2\t노트북\t1000000'),
(5, 'INSERT INTO products VALUES (1, ''연필'', 500), (2, ''지우개'', 300), (3, ''공책'', 1000), (4, ''볼펜'', 800)', '3\t공책\t1000'),
(5, 'INSERT INTO products VALUES (1, ''유일상품'', 9999)', '1\t유일상품\t9999'),
(5, 'INSERT INTO products VALUES (1, ''A'', 100), (2, ''B'', 200), (3, ''C'', 300), (4, ''D'', 400), (5, ''E'', 500)', '5\tE\t500');

-- Test Cases for Problem 6: 카테고리별 상품 통계
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(6, 'INSERT INTO products VALUES (1, ''사과'', ''과일''), (2, ''바나나'', ''과일''), (3, ''당근'', ''채소'')', '과일\t2\n채소\t1'),
(6, 'INSERT INTO products VALUES (1, ''스마트폰'', ''전자제품''), (2, ''노트북'', ''전자제품''), (3, ''셔츠'', ''의류'')', '의류\t1\n전자제품\t2');

-- Test Cases for Problem 7: 주문 내역 상세 조회
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(7, 'INSERT INTO users VALUES (1, ''김철수''), (2, ''이영희''); INSERT INTO orders VALUES (1, 1, ''스마트폰''), (2, 2, ''노트북'')', '1\t김철수\t1\t1\t스마트폰\n2\t이영희\t2\t2\t노트북'),
(7, 'INSERT INTO users VALUES (1, ''홍길동''); INSERT INTO orders VALUES (1, 1, ''책''), (2, 1, ''펜'')', '1\t홍길동\t1\t1\t책\n1\t홍길동\t2\t1\t펜');

-- Test Cases for Problem 8: 평균 이상 고객 찾기
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(8, 'INSERT INTO users VALUES (1, ''김철수'', 25), (2, ''이영희'', 30), (3, ''박민수'', 20)', '2\t이영희\t30'),
(8, 'INSERT INTO users VALUES (1, ''A'', 40), (2, ''B'', 20), (3, ''C'', 30)', '1\tA\t40');

-- Test Cases for Problem 9: 우수 고객 선별 (HAVING)
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(9, 'INSERT INTO orders VALUES (1, 1, 100), (2, 1, 200), (3, 2, 150)', '1'),
(9, 'INSERT INTO orders VALUES (1, 1, 100), (2, 2, 200), (3, 2, 150), (4, 2, 100)', '2');

-- Test Cases for Problem 10: 미주문 고객 찾기 (5개)
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(10, 'INSERT INTO users VALUES (1, ''김철수''), (2, ''이영희''), (3, ''박민수''); INSERT INTO orders VALUES (1, 1), (2, 1)', '2\t이영희\n3\t박민수'),
(10, 'INSERT INTO users VALUES (1, ''홍길동''), (2, ''김영수''); INSERT INTO orders VALUES (1, 2)', '1\t홍길동'),
(10, 'INSERT INTO users VALUES (1, ''A''), (2, ''B''), (3, ''C''), (4, ''D'')', '1\tA\n2\tB\n3\tC\n4\tD'),
(10, 'INSERT INTO users VALUES (1, ''주문자''), (2, ''미주문자''); INSERT INTO orders VALUES (1, 1), (2, 1), (3, 1)', '2\t미주문자'),
(10, 'INSERT INTO users VALUES (1, ''Solo''); INSERT INTO orders VALUES (1, 1)', '');

-- Test Cases for Problem 11: 부서별 급여 순위 (윈도우 함수)
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(11, 'INSERT INTO employees VALUES (1, ''김철수'', ''영업'', 5000), (2, ''이영희'', ''영업'', 6000), (3, ''박민수'', ''개발'', 7000)', '이영희\t영업\t6000\t1\n김철수\t영업\t5000\t2\n박민수\t개발\t7000\t1'),
(11, 'INSERT INTO employees VALUES (1, ''A'', ''인사'', 4000), (2, ''B'', ''인사'', 4500)', 'B\t인사\t4500\t1\nA\t인사\t4000\t2');

-- Test Cases for Problem 12: 조직도 탐색 (재귀 CTE)
INSERT INTO test_cases (problem_id, init_sql, answer) VALUES
(12, 'INSERT INTO employees VALUES (1, ''CEO'', NULL), (2, ''팀장'', 1), (3, ''개발자'', 2)', '2\t팀장\t1\n3\t개발자\t2'),
(12, 'INSERT INTO employees VALUES (1, ''대표'', NULL), (2, ''본부장'', 1), (3, ''팀장'', 2), (4, ''사원'', 3)', '2\t본부장\t1\n3\t팀장\t2\n4\t사원\t3');
