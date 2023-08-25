// 기본 데이터 입력
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA', 10000);

// 세션 1
set autocommit false;
update member set money = 500 where member_id = 'memberA';

// 세션 2
set lock_timeout 5000;
set autocommit false;
update member set money = 1000 where member_id = 'memberA';

// 세션 1
commit;

// 세션 2
commit;