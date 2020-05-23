package io.github.jzdayz.mbg.util;

public class Utils {

    interface Action{
        void doSomething();
    }

    public static boolean ex(Action action){
        try {
            action.doSomething();
            return false;
        }catch (Exception e){
            return true;
        }
    }
}
