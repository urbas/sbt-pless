package si.urbas.sbtutils.textfiles

import org.scalatest.{Matchers, WordSpec}
import TextFileManipulation.replaceVersionsInText

class TextFileManipulationTest extends WordSpec with Matchers {
  val groupId = "foo"
  val artifactId = "bar"
  val oldVersion = "0.0.1-SNAPSHOT"
  val newVersion = "1.2.3.4"

  "replaceVersionsInText" when {

    "given an empty string" must {
      "return an empty string" in {
        replaceVersionsInText("", "abc", "abc", "sda") shouldEqual ""
      }
    }

    "the string does not contain the target sbt coordinates" must {
      "return the unmodified string" in {
        val contentWithoutSbtCoordinates = "sdjasnda sadjas"
        replaceVersionsInText(contentWithoutSbtCoordinates, "abc", "abc", "sda") shouldEqual contentWithoutSbtCoordinates
      }
    }

    "the contains the target sbt coordinates" must {
      "replace the version" in {
        val initialContent = sbtCoordinates(groupId, artifactId, oldVersion)
        val expectedContent = sbtCoordinates(groupId, artifactId, newVersion)
        replaceVersionsInText(initialContent, groupId, artifactId, newVersion) shouldEqual expectedContent
      }
    }

  }

  def sbtCoordinates(groupId: String, artifactId: String, version: String): String = {
    s"""
        |"$groupId" %% "$artifactId" % "$version"
     """.stripMargin
  }

}
