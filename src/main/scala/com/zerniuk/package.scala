package com

package object zerniuk {

  def thread(body: => Unit) = {
    val t = new Thread {
      override def run(): Unit = body
    }
    t.start()
  }

  def sleep(sec: Int) = {
    Thread.sleep(sec * 1000)
  }
}
