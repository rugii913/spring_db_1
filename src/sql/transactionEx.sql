drop table member if exists;
create table member (
    member_id varchar(10),
    money     integer not null default 0,
    primary key (member_id)
);

// 1. 기본 데이터 입력
// 데이터 초기화
set autocommit true;
delete from member;
insert into member (member_id, money) values ('oldId', 10000);
// -> 세션 1, 세션 2에서 select * from member; 결과 같음 - autocommit ture 이므로

// 2. (세션 1)신규 데이터 추가 - 커밋 전
// 트랜잭션 시작
set autocommit false; // 수동 커밋 모드
insert into member(member_id, money) values ('newId1', 10000);
insert into member(member_id, money) values ('newId2', 10000);
// -> 세션 2에서 select * from member; 결과에는 변경 자료 보이지 않음 - 아직 세션 1이 커밋하지 않았음

// 3-1. (세션 1)커밋 - commit
commit; // 데이터베이스에 반영
select * from member;
// -> 세션 1이 커밋했기 때문에 모든 세션에서 변경된 데이터 조회 가능

// 3-2. (세션 1)롤백 - rollback
set autocommit true;
delete from member;
insert into member (member_id, money) values ('oldId', 10000);

set autocommit false; // 수동 커밋 모드
insert into member(member_id, money) values ('newId1', 10000);
insert into member(member_id, money) values ('newId2', 10000);

// 각 세션에서 확인 - 세션 1에만 변경 데이터 보임
select * from member;

rollback;

// 다시 각 세션에서 확인 - 데이터가 DB에 반영되지 않은 것을 볼 수 있음
select * from member;

// -------------------------------------------------------
// 계좌이체 예제
// 1. 계좌이체 정상
// 기본 데이터 입력
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA', 10000);
insert into member(member_id, money) values ('memberB', 10000);

// 계좌이체 실행 SQL - 성공
set autocommit false;
update member set money = 10000 - 2000 where member_id = 'memberA';
update member set money = 10000 + 2000 where member_id = 'memberB';

// 세션 1 커밋
commit;
select * from member; // 다른 세션에서도 변경 데이터 확인 가능

// 2. 계좌이체 문제 상황 - 커밋
// 기본 데이터 입력
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA', 10000);
insert into member(member_id, money) values ('memberB', 10000);

// 계좌이체 실행 SQL - 오류
set autocommit false;
update member set money = 10000 - 2000 where member_id = 'memberA'; // 성공
update member set money = 10000 + 2000 where member_iddd = 'memberB'; // 쿼리 예외 발생

// 세션 1 강제 커밋
commit;
select * from member;
// 이렇게 중간에 문제가 발생했을 때는 커밋을 호출하면 안 된다. 롤백을 호출해서 데이터를 트랜잭션 시작 시점으로 원복해야 한다.

// 3. 계좌이체 문제 상황 - 롤백
// 기본 데이터 입력
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA', 10000);
insert into member(member_id, money) values ('memberB', 10000);

// 계좌이체 실행 SQL - 오류
set autocommit false;
update member set money = 10000 - 2000 where member_id = 'memberA'; // 성공
update member set money = 10000 + 2000 where member_iddd = 'memberB'; // 쿼리 예외 발생

// 세션 1 롤백
rollback;
select * from member;
// 이럴 때는 롤백을 호출해서 트랜잭션을 시작하기 전 단계로 데이터를 복구해야 한다.
