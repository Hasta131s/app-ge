package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.ui.KayryptViewModel
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertNotNull(context)
  }

  @Test
  fun `test viewModel instantiation`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    assertNotNull(app)
    val viewModel = KayryptViewModel(app)
    assertNotNull(viewModel)
  }
}
