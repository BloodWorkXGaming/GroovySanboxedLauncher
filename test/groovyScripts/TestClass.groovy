package groovyScripts

class TestClassGroovy{
    TestClassGroovy(){
        println "default"
    }
    TestClassGroovy(String s) {
        println "hi i am a constructor $s"
    }
    static int testInt = 20
}

class Teee{
    Teee(int i){
        println i
    }
}