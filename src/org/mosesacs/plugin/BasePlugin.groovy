package org.mosesacs.plugin

abstract class BasePlugin implements PluginInterface {

    boolean test() {
        println "test nella classe astratta"
        return true
    }

}
