package si.urbas.sbtutils.releases

import org.scalatest.{Matchers, WordSpec}
import ReleaseProcessTransformation._
import sbtrelease.ReleaseStep

class ReleaseProcessTransformationTest extends WordSpec with Matchers {

  "inserting a release step before the second release step" must {
    "produce a corresponding release process" in new TestSetup {
      val newReleaseProcess = insertReleaseSteps(newReleaseStep).before(existingReleaseStepSecond).in(existingReleaseProcess)
      newReleaseProcess should contain theSameElementsInOrderAs List(
        existingReleaseStepFirst,
        newReleaseStep,
        existingReleaseStepSecond,
        existingReleaseStepLast
      )
    }
  }

  "inserting a release step after the second release step" must {
    "produce a corresponding release process" in new TestSetup {
      val newReleaseProcess = insertReleaseSteps(newReleaseStep).after(existingReleaseStepSecond).in(existingReleaseProcess)
      newReleaseProcess should contain theSameElementsInOrderAs List(
        existingReleaseStepFirst,
        existingReleaseStepSecond,
        newReleaseStep,
        existingReleaseStepLast
      )
    }
  }

  "replacing a non-existent release step" must {
    "throw an exception" in new TestSetup {
      intercept[IllegalArgumentException] {
        replaceReleaseStep(newReleaseStep).withSteps(null).in(existingReleaseProcess)
      }
    }
  }

  "replacing the second release step" must {
    "produce a corresponding release process" in new TestSetup {
      val newReleaseProcess = replaceReleaseStep(existingReleaseStepSecond).withSteps(newReleaseStep).in(existingReleaseProcess)
      newReleaseProcess should contain theSameElementsInOrderAs List(
        existingReleaseStepFirst,
        newReleaseStep,
        existingReleaseStepLast
      )
    }
  }

  "chaining insertion after replacement" must {
    "produce a corresponding release process" in new TestSetup {
      val newReleaseProcess = replaceReleaseStep(existingReleaseStepSecond).withSteps(newReleaseStep)
        .insertReleaseSteps(existingReleaseStepSecond).after(newReleaseStep)
        .in(existingReleaseProcess)
      newReleaseProcess should contain theSameElementsInOrderAs List(
        existingReleaseStepFirst,
        newReleaseStep,
        existingReleaseStepSecond,
        existingReleaseStepLast
      )

    }
  }

  "chaining replacement after insertion" must {
    "produce a corresponding release process" in new TestSetup {
      val newReleaseProcess = insertReleaseSteps(newReleaseStep).after(existingReleaseStepSecond)
      .replaceReleaseStep(newReleaseStep).withSteps(existingReleaseStepFirst)
        .in(existingReleaseProcess)

      newReleaseProcess should contain theSameElementsInOrderAs List(
        existingReleaseStepFirst,
        existingReleaseStepSecond,
        existingReleaseStepFirst,
        existingReleaseStepLast
      )
    }
  }

  "removing release steps" must {
    "produce a release process without removed steps" in new TestSetup {
      val newReleaseProcess = removeReleaseSteps(existingReleaseStepSecond)
        .in(existingReleaseProcess)

      newReleaseProcess should contain theSameElementsInOrderAs List(
        existingReleaseStepFirst,
        existingReleaseStepLast
      )
    }
  }


  class TestSetup {
    val newReleaseStep = ReleaseStep(state => state)
    val newTaskKey = sbt.taskKey("fake-task")
    val existingReleaseStepFirst = ReleaseStep(state => state)
    val existingReleaseStepSecond = ReleaseStep(state => state)
    val existingReleaseStepLast = ReleaseStep(state => state)
    val existingReleaseProcess = Seq(
      existingReleaseStepFirst,
      existingReleaseStepSecond,
      existingReleaseStepLast
    )
  }

}
