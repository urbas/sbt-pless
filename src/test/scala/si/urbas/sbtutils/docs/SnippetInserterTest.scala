package si.urbas.sbtutils.docs

import org.scalatest.WordSpec
import org.scalatest.Matchers._
import java.io.FileNotFoundException
import sbt.IO._
import sbt._

class SnippetInserterTest extends WordSpec {

  private val SNIPPET_FOO_LINE_1 = "I'm in the snippet"
  private val SNIPPET_FOO_LINE_2 = "So am I"
  private val SNIPPET_FOO_NAME = "foo"
  private val SNIPPET_FOO_START = s"SNIPPET:$SNIPPET_FOO_NAME"
  private val SNIPPET_FOO_END = s"ENDSNIPPET:$SNIPPET_FOO_NAME"
  private val SNIPPET_BAR_NAME = "bar"
  private val SNIPPET_BAR_START = s"SNIPPET: $SNIPPET_BAR_NAME"
  private val SNIPPET_BAR_END = s"ENDSNIPPET: $SNIPPET_BAR_NAME"
  private val SNIPPET_BAR_LINE_1 = "I'm in the bar snippet"

  private val linesWithoutSnippet = List("first line", "second line", "third line")
  private val emptyFooSnippet = List(SNIPPET_FOO_START, SNIPPET_FOO_END)
  private val barSnippet = List(SNIPPET_BAR_START, SNIPPET_BAR_LINE_1, SNIPPET_BAR_END)
  private val fooSnippetContent = List(SNIPPET_FOO_LINE_1, SNIPPET_FOO_LINE_2)
  private val linesWithNonEmptySnippet = (linesWithoutSnippet :+ SNIPPET_FOO_START) ++ fooSnippetContent ++ (SNIPPET_FOO_END +: linesWithoutSnippet)
  private val linesWithNestedSnippet = (linesWithoutSnippet :+ SNIPPET_FOO_START) ++ fooSnippetContent ++ barSnippet ++ fooSnippetContent ++ (SNIPPET_FOO_END +: linesWithoutSnippet)

  "reading a snippet from a file" when {
    "the file does not exist" must {
      "throw an exception" in {
        withDirectorySetup {
          snippetInserter =>
            intercept[FileNotFoundException] {
              snippetInserter.snippet("nonExistentFile", SNIPPET_FOO_NAME)
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
                val foundSnippet = snippetInserter.snippet(snippetFile.getCanonicalPath, SNIPPET_FOO_NAME)
                foundSnippet shouldBe fooSnippetContent.mkString("\n")
            }
        }
      }
    }
  }

  "getting lines within a snippet" when {
    "no line starts the snippet" must {
      "return an empty iterable" in {
        val foundLines = SnippetInserter.linesWithinSnippet(linesWithoutSnippet, SNIPPET_FOO_NAME)
        foundLines shouldBe empty
      }
    }

    "the snippet contains no line" must {
      "return an empty iterable" in {
        val foundLines = SnippetInserter.linesWithinSnippet(linesWithoutSnippet ++ emptyFooSnippet ++ linesWithoutSnippet, SNIPPET_FOO_NAME)
        foundLines shouldBe empty
      }
    }

    "the snippet contains some lines" must {
      "return those lines" in {
        val foundLines = SnippetInserter.linesWithinSnippet(linesWithNonEmptySnippet, SNIPPET_FOO_NAME)
        foundLines.toList should contain theSameElementsInOrderAs fooSnippetContent
      }
    }

    "the snippet contains a nested snippet" must {
      "remove the start and end tags of the nested snippet" in {
        val foundLines = SnippetInserter.linesWithinSnippet(linesWithNestedSnippet, SNIPPET_FOO_NAME)
        foundLines.toList should contain theSameElementsInOrderAs (fooSnippetContent ++ Seq(SNIPPET_BAR_LINE_1) ++ fooSnippetContent)
      }
    }
  }

  private def withDirectorySetup(testBody: (SnippetInserter) => Unit): Unit = {
    withTemporaryDirectory {
      tmpDirectory =>
        testBody(new SnippetInserter(Seq(tmpDirectory, new File(tmpDirectory, "templateFile"))))
    }
  }
}
