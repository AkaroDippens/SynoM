import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.synomprojectkotlin.AdminActivity
import com.example.synomprojectkotlin.DataBaseHelper
import com.example.synomprojectkotlin.LoginActivity
import com.example.synomprojectkotlin.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.synomprojectkotlin.models.Users
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.auth.User
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    private lateinit var scenario: ActivityScenario<LoginActivity>
    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var mockFirebaseUser: FirebaseUser
    private lateinit var mockDataBaseHelper: DataBaseHelper

    @Before
    fun setup() {
        // Initialize mocks
        mockFirebaseAuth = mock(FirebaseAuth::class.java)
        mockFirebaseUser = mock(FirebaseUser::class.java)
        mockDataBaseHelper = mock(DataBaseHelper::class.java)

        // Mock FirebaseAuth behavior
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.email).thenReturn("mirzoev.nidzhat@mail.ru")

    }

    @Test
    fun testAdminLogin() {
        scenario.onActivity { activity ->
            // Inject mocks
            activity.auth = mockFirebaseAuth
            activity.dataBaseHelper = mockDataBaseHelper

            // Simulate admin login
            val user = User("admin")


            activity.onCreate(null)

            // Verify that the correct intent was started
            val startedIntent = shadowOf(activity).nextStartedActivity
            val shadowIntent = shadowOf(startedIntent)
            assertEquals(AdminActivity::class.java, shadowIntent.intentClass)
        }
    }

    @Test
    fun testRegularUserLogin() {
        scenario.onActivity { activity ->
            // Inject mocks
            activity.auth = mockFirebaseAuth
            activity.dataBaseHelper = mockDataBaseHelper

            // Simulate regular user login
            val user = User("user")
            `when`(mockFirebaseUser.email).thenReturn("user@example.com")

            activity.onCreate(null)

            // Verify that the correct intent was started
            val startedIntent = shadowOf(activity).nextStartedActivity
            val shadowIntent = shadowOf(startedIntent)
            assertEquals(MainActivity::class.java, shadowIntent.intentClass)
        }
    }
}