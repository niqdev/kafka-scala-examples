package com.kafka.demo

import java.io.File

object Files {

  def initFile(path: String): File = {
    val file = new File(path)
    // creates parent directories if don't exist
    file.getParentFile.mkdirs()
    file
  }

}
