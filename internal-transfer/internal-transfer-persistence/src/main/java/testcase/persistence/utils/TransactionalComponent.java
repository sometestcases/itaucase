package testcase.persistence.utils;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionalComponent {

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void require(Runnable runnable){
        runnable.run();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public <T> T require(Supplier<T> supplier){
        return supplier.get();
    }
}
