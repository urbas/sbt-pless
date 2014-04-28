package si.urbas.sbtutils.docs

import org.scalatest.WordSpec
import org.scalatest.Matchers._
import java.io.FileNotFoundException
import sbt.IO._
import sbt._

class SnippetInserterTest extends WordSpec {

  val snippetNameFoo = "foo"
  val snippetFooStart = s"SNIPPET:$snippetNameFoo"
  val snippetFooEnd = s"ENDSNIPPET:$snippetNameFoo"
  val linesWithoutSnippet = List("first line", "second line", "third line")
  val emptySnippet = List(snippetFooStart, snippetFooEnd)
  private val SNIPPET_LINE_1 = "I'm in the snippet"
  private val SNIPPET_LINE_2 = "So am I"
  val linesWithinSnippet = List(SNIPPET_LINE_1, SNIPPET_LINE_2)
  val linesWithNonEmptySnippet = (linesWithoutSnippet :+ snippetFooStart) ++ linesWithinSnippet ++ (snippetFooEnd +: linesWithoutSnippet)

  def withDirectorySetup(testBody: (SnippetInserter) => Unit): Unit = {
    withTemporaryDirectory {
      tmpDirectory =>
        testBody(new SnippetInserter(tmpDirectory, new File(tmpDirectory, "templateFile")))
    }
  }

  "reading a snippet from a file" when {
    "the file does not exist" must {
      "throw an exception" in {
        withDirectorySetup {
          snippetInserter =>
            intercept[FileNotFoundException] {
              snippetInserter.snippet("nonExistentFile", snippetNameFoo)
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
                val foundSnippet = snippetInserter.snippet(snippetFile.getCanonicalPath, snippetNameFoo)
                foundSnippet shouldBe linesWithinSnippet.mkString("\n")
            }
        }
      }
    }
  }

  "getting lines within a snippet" when {
    "no line contains the snippet" must {
      "return an empty iterable" in {
        val foundLines = SnippetInserter.linesWithinSnippet(linesWithoutSnippet, snippetNameFoo)
        foundLines shouldBe empty
      }
    }

    "the snippet contains no line" must {
      "return an empty iterable" in {
        val foundLines = SnippetInserter.linesWithinSnippet(linesWithoutSnippet ++ emptySnippet ++ linesWithoutSnippet, snippetNameFoo)
        foundLines shouldBe empty
      }
    }

    "the snippet contains some lines" must {
      "return those lines" in {
        val foundLines = SnippetInserter.linesWithinSnippet(linesWithNonEmptySnippet, snippetNameFoo)
        foundLines.toList should contain theSameElementsInOrderAs linesWithinSnippet
      }
    }
  }

}
