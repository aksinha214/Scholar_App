package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.auth.ScholarAuthManager
import com.example.data.model.UserProfileEntity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ScholarAuthAndProfileTest {

    private lateinit var context: Context
    private lateinit var authManager: ScholarAuthManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Clear prefs before test
        context.getSharedPreferences("cs_scholar_accounts", Context.MODE_PRIVATE).edit().clear().commit()
        context.getSharedPreferences("cs_scholar_session", Context.MODE_PRIVATE).edit().clear().commit()
        authManager = ScholarAuthManager(context)
    }

    @Test
    fun testDemoAccountIsSeeded() {
        val demoAccount = authManager.getAccount("alexei.chen@ysu.edu.cn")
        assertNotNull("Demo account should be pre-seeded", demoAccount)
        assertEquals("Alexei Chen-Kovalenko", demoAccount?.fullName)
        assertFalse("Password hash must not be plain text", demoAccount?.passwordHash == "ysu_scholar_2026")

        // Demo login should succeed with correct demo password
        val loginResult = authManager.signIn("alexei.chen@ysu.edu.cn", "ysu_scholar_2026")
        assertTrue("Demo login should succeed with valid password", loginResult.isSuccess)
    }

    @Test
    fun testSignUpValidationRules() {
        // Blank name
        val blankName = authManager.signUp("", "test@ysu.edu.cn", "2024CS001", "pass123", "pass123")
        assertTrue(blankName.isFailure)
        assertTrue(blankName.exceptionOrNull()?.message?.contains("full name", ignoreCase = true) == true)

        // Invalid email
        val badEmail = authManager.signUp("Scholar Name", "notanemail", "2024CS001", "pass123", "pass123")
        assertTrue(badEmail.isFailure)
        assertTrue(badEmail.exceptionOrNull()?.message?.contains("valid email", ignoreCase = true) == true)

        // Password too short
        val shortPass = authManager.signUp("Scholar Name", "test@ysu.edu.cn", "2024CS001", "123", "123")
        assertTrue(shortPass.isFailure)
        assertTrue(shortPass.exceptionOrNull()?.message?.contains("at least 6", ignoreCase = true) == true)

        // Passwords do not match
        val mismatch = authManager.signUp("Scholar Name", "test@ysu.edu.cn", "2024CS001", "pass123", "differentPass")
        assertTrue(mismatch.isFailure)
        assertTrue(mismatch.exceptionOrNull()?.message?.contains("match", ignoreCase = true) == true)
    }

    @Test
    fun testRealUserSignUpAndSignInFlow() {
        val signUpResult = authManager.signUp(
            name = "Jane Doe",
            email = "jane.doe@ysu.edu.cn",
            studentId = "2024CS9999",
            password = "securePassword123",
            confirmPassword = "securePassword123",
            nationality = "Germany",
            degree = "Master of Science in Computer Science"
        )

        assertTrue("Sign up should succeed with valid parameters", signUpResult.isSuccess)
        val account = signUpResult.getOrThrow()
        assertEquals("Jane Doe", account.fullName)
        assertEquals("jane.doe@ysu.edu.cn", account.email)
        assertEquals("2024CS9999", account.studentId)
        assertEquals("Germany", account.nationality)
        assertEquals("Master of Science in Computer Science", account.degree)
        assertFalse("Password must be securely hashed", account.passwordHash.contains("securePassword123"))

        // Duplicate registration must fail
        val duplicateResult = authManager.signUp(
            name = "Jane Doe Duplicate",
            email = "jane.doe@ysu.edu.cn",
            studentId = "2024CS9999",
            password = "anotherPassword",
            confirmPassword = "anotherPassword"
        )
        assertTrue("Duplicate account creation must fail", duplicateResult.isFailure)

        // Sign in with wrong password
        val wrongPass = authManager.signIn("jane.doe@ysu.edu.cn", "wrongPassword")
        assertTrue("Sign in with invalid password must fail", wrongPass.isFailure)

        // Sign in with correct password (via email)
        val validSignInEmail = authManager.signIn("jane.doe@ysu.edu.cn", "securePassword123")
        assertTrue("Sign in with email should succeed", validSignInEmail.isSuccess)

        // Sign in with correct password (via student ID)
        val validSignInId = authManager.signIn("2024CS9999", "securePassword123")
        assertTrue("Sign in with student ID should succeed", validSignInId.isSuccess)
    }

    @Test
    fun testProfileEditingAndPersistence() {
        authManager.signUp(
            name = "Maya Lin",
            email = "maya.lin@ysu.edu.cn",
            studentId = "2024CS8888",
            password = "scholarPass2026",
            confirmPassword = "scholarPass2026"
        )

        val account = authManager.getAccount("maya.lin@ysu.edu.cn")
        assertNotNull(account)

        val updatedProfile = account!!.profile.copy(
            name = "Dr. Maya Lin",
            nationality = "Singapore",
            degree = "PhD in Computer Science",
            researchInterests = "Multi-Modal AI, Reinforcement Learning",
            chineseProficiency = "HSK 6"
        )

        val updated = authManager.updateAccountProfile("maya.lin@ysu.edu.cn", updatedProfile)
        assertTrue("Profile update must return true", updated)

        // Re-read account to verify persistence
        val reloaded = authManager.getAccount("maya.lin@ysu.edu.cn")
        assertNotNull(reloaded)
        assertEquals("Dr. Maya Lin", reloaded?.fullName)
        assertEquals("Singapore", reloaded?.profile?.nationality)
        assertEquals("PhD in Computer Science", reloaded?.profile?.degree)
        assertEquals("Multi-Modal AI, Reinforcement Learning", reloaded?.profile?.researchInterests)
        assertEquals("HSK 6", reloaded?.profile?.chineseProficiency)
    }
}
