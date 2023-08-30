package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@Slf4j
public class UncheckedAppTest {

    @Test
    void unchecked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(controller::request).isInstanceOf(Exception.class); // 메서드 호출 순서 상 SQLException 터질 것
    }

    @Test
    void printEx() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            // e.printStackTrace(); // -> System.out에 스택 트레이스 출력, 좋지 않음 // 실무에서는 항상 로그를 사용해야 함
            log.info("ex", e);
        }
    }

    static class Controller {
        Service service = new Service();

        public void request() { // 둘 다 런타임 예외를 던지므로 컴파일러가 체크하지 않는다.
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() { // 둘 다 런타임 예외를 던지므로 컴파일러가 체크하지 않는다.
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }
    }

    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) { // 예외를 잡고 전환해서 다시 던질 때 기존 예외를 포함시켜야 stack trace를 확인할 수 있다.
                // throw new RuntimeSQLException(); // 기존 예외(e) 제외 - root cause를 확인할 수 없는 문제 
                // -> 실제 DB에 연동했다면 DB에서 발생한 예외의 정보를 전혀 확인할 수가 없다.
                throw new RuntimeSQLException(e); // 기존 예외(e) 포함
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException() {
            super();
        }

        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
