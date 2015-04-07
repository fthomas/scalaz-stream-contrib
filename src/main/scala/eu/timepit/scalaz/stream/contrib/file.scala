package eu.timepit.scalaz.stream.contrib

import java.nio.file.attribute.FileAttribute
import java.nio.file.{ Files, Path }

import scalaz.concurrent.Task
import scalaz.stream.Process
import scalaz.stream.Process._

object file {

  def tempFile(prefix: String, suffix: String, attrs: FileAttribute[_]*): Process[Task, Path] = {
    val create = Task.delay(Files.createTempFile(prefix, suffix, attrs: _*))
    await(create) { path =>
      val delete = eval(Task.delay(Files.delete(path))).drain
      emit(path).onComplete(delete)
    }
  }

}
