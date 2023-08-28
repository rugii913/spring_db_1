package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    // private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // Connection con = dataSource.getConnection();
        //트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition()); // TransactionDefinition: 트랜잭션 관련 속성

        try {
            // con.setAutoCommit(false);
            // 비즈니스 로직
            bizLogic(fromId, toId, money); // bizLogic(con, fromId, toId, money);
            transactionManager.commit(status); // 성공 시 커밋 // con.commit();
        } catch (Exception e) {
            transactionManager.rollback(status); // 실패 시 롤백 // con.rollback();
            throw new IllegalStateException(e);
        }/* finally {
            release(con); // commit 혹은 rollback 될 때 트랜잭션 매니저가 자원 정리해 줌
        }*/
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    /*
    private void release(Connection con) { // commit 혹은 rollback 될 때 트랜잭션 매니저가 자원 정리해 줌
        if (con != null) {
            try {
                con.setAutoCommit(true); // 커넥션 풀 고려
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }
    */

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
