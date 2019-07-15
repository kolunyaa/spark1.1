package com.zerniuk.service

import java.io.File
import java.nio.file.{Files, Paths}

import scala.collection.mutable.Stack
import scala.util.Try

class FileService {
  val newFiles = Stack(
    new File("data/retail/new/2010-12-03.csv"),
    new File("data/retail/new/2010-12-05.csv"),
    new File("data/retail/new/2010-12-06.csv")
  )


  def addNewFiles = {
    while (newFiles.nonEmpty) {
      Thread.sleep(5 * 1000l)
      val file = newFiles.pop()
      Try {
        val temp = Files.copy(Paths.get(file.getPath),
          Paths.get(file.getPath.replace("new", "by-day")))
        System.out.println(s"File $temp added")
      }
    }
  }

}
