package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UncheckedTest {

    @Test
    void checked_catch() {
        Service service = new Service();
        service.callCatch(); // service에서 예외를 잡았기 때문에 이 메서드까지 예외가 올라오지 않음
    }

    @Test
    void checked_throw() {
        Service service = new Service();
        assertThatThrownBy(service::callThrow).isInstanceOf(MyUncheckedException.class);
    }

    /**
     * RuntimeException을 상속한 예외는 언체크 예외가 된다.
     */
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) { // 내부에 메시지를 갖는 생성자 - 무엇 때문에 예외가 발생하는지 담는다.
            super(message);
        }
    }

    /**
     * Unchecked 예외는
     * 예외를 잡거나, (명시적으로) 던지지 않아도 된다.
     * 예외를 잡지 않으면 자동으로 밖으로 던진다.
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 필요한 경우 예외를 잡아서 처리하면 된다.
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                // 예외 처리 로직
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        /**
         * 예외를 잡지 않아도 된다. 자연스럽게 상위로 넘어간다.
         * 체크 예외와 다르게 (throws 예외) 선언을 하지 않아도 된다.
         */
        public void callThrow() {
            repository.call();
        }
    }

    static class Repository {
        public void call() { // 던질 때 (throws 예외) 선언을 생략해도 된다.
            throw new MyUncheckedException("ex");
        }
    }
}
