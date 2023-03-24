package cc.oolong;

import org.springframework.stereotype.Service;
import cc.oolong.Main;

@Service
public class FooService {
    private final Main.Foo foo;

    public FooService(Main.Foo foo) {
        this.foo = foo;
        System.out.println();
    }

    public String getFoolName() {
        return this.foo.name();
    }
}
