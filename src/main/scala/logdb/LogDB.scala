package logdb

import java.io._

import scala.collection.SortedMap
import scala.collection.mutable.ArrayBuffer

class LogDB(dir: File) {

  private var memtable: SortedMap[Int, Int] = SortedMap()

  // restore data during LogDB init
  {
    memtable = loadAllData()
    println("membtable: " + memtable)
  }

  def save(key: Int, value: Int): Unit = {
    val out = new FileOutputStream(dir, true)
    val serializedData = serialize(key, value)
    try {
      out.write(serializedData)
      out.getFD.sync()
    } finally {
      out.close()
    }

    memtable += (key -> value)
  }

  def get(key: Int): Option[Int] = memtable.get(key)

  private[this] def loadAllData(): SortedMap[Int, Int] = {
    var updates: SortedMap[Int, Int] = SortedMap()

    val raf = new RandomAccessFile(dir, "r")
    raf.seek(0)
    val fileSize: Long = raf.length()
    var offset: Long = 0

    while(offset < fileSize) {
      val key = raf.readInt()
      val value = raf.readInt()
      offset = raf.getFilePointer
      updates += ((key, value))
    }

    updates
  }

  private[this] def serialize(key: Int, value: Int): Array[Byte] = {
    val out = new ByteArrayOutputStream()
    val out2 = new DataOutputStream(out)

    out2.writeInt(key)
    out2.writeInt(value)

    out.toByteArray
  }
}

object LogDB {
  def apply(path: String): LogDB = new LogDB(new File(path))
}
