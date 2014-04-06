package si.urbas.sbtutils.releases

import org.scalatest.{Matchers, WordSpec}
import ReleaseProcessTransformation._
import sbtrelease.ReleaseStep

class ReleaseProcessTransformationTest extends WordSpec with Matchers {
  "inserting a release step at the front of the release process" must {
    "throw an exception" in new TestSetup {
      intercept[IllegalArgumentException] {
        insertGlobalTasks(newTaskKey).before(existingReleaseStepFirst).in(existingReleaseProcess)
      }
    }
  }

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

  "inserting a release step at the back of the release process" must {
    "throw an exception" in new TestSetup {
      intercept[IllegalArgumentException] {
        insertGlobalTasks(newTaskKey).after(existingReleaseStepLast).in(existingReleaseProcess)
      }
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
        replaceReleaseStep(newReleaseStep).withReleaseSteps(null).in(existingReleaseProcess)
      }
    }
  }

  "replacing the second release step" must {
    "produce a corresponding release process" in new TestSetup {
      val newReleaseProcess = replaceReleaseStep(existingReleaseStepSecond).withReleaseSteps(newReleaseStep).in(existingReleaseProcess)
      newReleaseProcess should contain theSameElementsInOrderAs List(
        existingReleaseStepFirst,
        newReleaseStep,
        existingReleaseStepLast
      )
    }
  }

  "chaining insertion after replacement" must {
    "produce a corresponding release process" in new TestSetup {
      val newReleaseProcess = replaceReleaseStep(existingReleaseStepSecond).withReleaseSteps(newReleaseStep)
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
      .replaceReleaseStep(newReleaseStep).withReleaseSteps(existingReleaseStepFirst)
        .in(existingReleaseProcess)
      newReleaseProcess should contain theSameElementsInOrderAs List(
        existingReleaseStepFirst,
        existingReleaseStepSecond,
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
