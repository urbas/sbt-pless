package si.urbas.sbtutils.docs

import org.scalatest.WordSpec
import org.scalatest.Matchers._
import java.io.FileNotFoundException
import sbt.IO._
import sbt._

class SnippetInserterTest extends WordSpec {

  private val SNIPPET_NAME_FOO = "foo"
  private val SNIPPET_LINE_1 = "I'm in the snippet"
  private val SNIPPET_LINE_2 = "So am I"
  private val SNIPPET_FOO_START = s"SNIPPET:$SNIPPET_NAME_FOO"
  private val SNIPPET_FOO_END = s"ENDSNIPPET:$SNIPPET_NAME_FOO"

  private val linesWithoutSnippet = List("first line", "second line", "third line")
  private val emptySnippet = List(SNIPPET_FOO_START, SNIPPET_FOO_END)
  private val linesWithinSnippet = List(SNIPPET_LINE_1, SNIPPET_LINE_2)
  private val linesWithNonEmptySnippet = (linesWithoutSnippet :+ SNIPPET_FOO_START) ++ linesWithinSnippet ++ (SNIPPET_FOO_END +: linesWithoutSnippet)

  "reading a snippet from a file" when {
    "the file does not exist" must {
      "throw an exception" in {
        withDirectorySetup {
          snippetInserter =>
            intercept[FileNotFoundException] {
              snippetInserter.snippet("nonExistentFile", SNIPPET_NAME_FOO)
            }
        }
      }
    }

    "the file exists" must {
      "return the snippet that's contained in the file" in {
        withDirectorySetup {
          snippetInserter =>
            withTemporaryFile("foo", "bar") {
              snippetFile =>
                writeLines(snippetFile, linesWithNonEmptySnippet)
                val foundSnippet = snippetInserter.snippet(snippetFile.getCanonicalPath, SNIPPET_NAME_FOO)
                foundSnippet shouldBe linesWithinSnippet.mkString("\n")
            }
        }
      }
    }
  }

  "getting lines within a snippet" when {
    "no line contains the snippet" must {
      "return an empty iterable" in {
        val foundLines = SnippetInserter.linesWithinSnippet(linesWithoutSnippet, SNIPPET_NAME_FOO)
        foundLines shouldBe empty
      }
    }

    "the snippet contains no line" must {
      "return an empty iterable" in {
        val foundLines = SnippetInserter.linesWithinSnippet(linesWithoutSnippet ++ emptySnippet ++ linesWithoutSnippet, SNIPPET_NAME_FOO)
        foundLines shouldBe empty
      }
    }

    "the snippet contains some lines" must {
      "return those lines" in {
        val foundLines = SnippetInserter.linesWithinSnippet(linesWithNonEmptySnippet, SNIPPET_NAME_FOO)
        foundLines.toList should contain theSameElementsInOrderAs linesWithinSnippet
      }
    }
  }

  private def withDirectorySetup(testBody: (SnippetInserter) => Unit): Unit = {
    withTemporaryDirectory {
      tmpDirectory =>
        testBody(new SnippetInserter(tmpDirectory, new File(tmpDirectory, "templateFile")))
    }
  }
}
