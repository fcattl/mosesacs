package org.mosesacs

class Event {

    def id
    def message
    def params

    def Event() {
        Random rand = new Random()
        this.id = rand.nextInt(100000)
    }

}

