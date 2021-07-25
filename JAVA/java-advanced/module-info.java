module info.kgeorgiy.ja.dziuba {
    requires java.compiler;
    requires info.kgeorgiy.java.advanced.student;
    requires info.kgeorgiy.java.advanced.crawler;
    requires info.kgeorgiy.java.advanced.hello;
    opens info.kgeorgiy.ja.dziuba.arrayset;
    opens info.kgeorgiy.ja.dziuba.student;
    opens info.kgeorgiy.ja.dziuba.walk;
    opens info.kgeorgiy.ja.dziuba.mapper;
    opens info.kgeorgiy.ja.dziuba.concurrent;
    opens info.kgeorgiy.ja.dziuba.crawler;
    opens info.kgeorgiy.ja.dziuba.hello;
    opens info.kgeorgiy.ja.dziuba.implementor;
    opens info.kgeorgiy.ja.dziuba.bank;
    opens info.kgeorgiy.ja.dziuba.i18n;
    requires info.kgeorgiy.java.advanced.implementor;
    requires java.rmi;
}