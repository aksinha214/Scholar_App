package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.UserProfileEntity
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom

data class ScholarAccount(
    val email: String,
    val studentId: String,
    val fullName: String,
    val passwordHash: String,
    val salt: String,
    val university: String,
    val department: String,
    val degree: String,
    val nationality: String,
    val createdAt: Long = System.currentTimeMillis(),
    val profile: UserProfileEntity
)

class ScholarAuthManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("cs_scholar_accounts", Context.MODE_PRIVATE)

    init {
        // Pre-seed the demo account for Alexei Chen-Kovalenko if it does not exist
        if (!hasAccount("alexei.chen@ysu.edu.cn", "2024CS0892")) {
            seedDemoAccount()
        }
    }

    private fun seedDemoAccount() {
        val salt = generateSalt()
        val hash = hashPassword("ysu_scholar_2026", salt)
        val demoProfile = UserProfileEntity(
            id = 1,
            name = "Alexei Chen-Kovalenko",
            nationality = "International Student (Kazakhstan / Central Asia)",
            university = "Yanshan University (燕山大学, Qinhuangdao, China)",
            department = "School of Information Science and Engineering (信息科学与工程学院)",
            degree = "Bachelor of Engineering in Computer Science and Technology (计算机科学与技术)",
            currentSemester = "Year 3, Semester 1 (Fall 2026)",
            expectedGraduation = "June 2027",
            researchInterests = "Computer Vision, 3D LiDAR Perception, Vision Transformers, Adverse Weather Sensing",
            technicalSkills = "PyTorch, OpenCV, CUDA, C++, Python, Linux (Ubuntu), Git, Docker, ROS, TensorRT",
            programmingLanguages = "Python (Expert), C/C++ (Intermediate-Advanced), Java (Intermediate), SQL, Bash",
            aiMlSkills = "Deep Learning, CNNs, Vision Transformers (ViT), Semantic Segmentation, Object Detection",
            cvSkills = "Point Cloud Processing (Open3D, PointNet++), Multi-View Geometry, Optical Flow",
            researchExperience = "Undergraduate Research Fellow at Yanshan Intelligent Information Processing Lab",
            publications = "1 under review at ACCV 2026; Working on CVPR 2027 submission",
            projects = "LiDAR-FogNet: 3D Point Cloud Semantic Segmentation in Maritime Coastal Fog",
            githubUrl = "https://github.com/alexei-ysu-cs",
            certifications = "DeepLearning.AI Deep Learning Specialization; NVIDIA DLI Fundamentals of Deep Learning",
            chineseProficiency = "HSK 4 (Certified, Score: 254/300); Target HSK 5",
            englishProficiency = "Fluent / IELTS Academic 7.5 (C1 Level)",
            careerGoals = "Pursue a direct PhD or Master's at a top AI Lab or work as an AI Algorithm / CV Engineer",
            targetIndustries = "Autonomous Driving, Robotics Perception, Industrial AI Inspection, Intelligent Systems",
            targetCountries = "China, Singapore, Germany, Canada, Switzerland",
            targetCompanies = "DeepSeek, Baidu Apollo, DJI, Tencent AI Lab, Huawei Noah's Ark, SenseTime",
            targetUniversities = "Tsinghua University (THU), Zhejiang University (ZJU), USTC, SJTU, NUS",
            targetVenues = "CVPR, ICCV, ECCV, NeurIPS, IEEE TPAMI, IEEE TIP",
            currentAcademicTasks = "Complete xv6 OS file system lab; Prepare Computer Vision term presentation",
            currentResearchProjects = "Point Cloud Fog-Robustness Benchmark on Qinhuangdao Port LiDAR dataset"
        )
        val demoAccount = ScholarAccount(
            email = "alexei.chen@ysu.edu.cn",
            studentId = "2024CS0892",
            fullName = "Alexei Chen-Kovalenko",
            passwordHash = hash,
            salt = salt,
            university = "Yanshan University (燕山大学)",
            department = "School of Information Science and Engineering",
            degree = "Bachelor of Engineering in CS & Technology",
            nationality = "International Student (Kazakhstan / Central Asia)",
            profile = demoProfile
        )
        saveAccount(demoAccount)
    }

    fun hasAccount(email: String, studentId: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        val cleanId = studentId.trim().lowercase()
        val accounts = getAllAccounts()
        return accounts.any {
            it.email.trim().lowercase() == cleanEmail ||
            (cleanId.isNotEmpty() && it.studentId.trim().lowercase() == cleanId)
        }
    }

    fun signUp(
        name: String,
        email: String,
        studentId: String,
        password: String,
        confirmPassword: String,
        university: String = "Yanshan University (燕山大学)",
        department: String = "School of Information Science and Engineering",
        degree: String = "Bachelor of Engineering in CS & Technology",
        nationality: String = "International Student"
    ): Result<ScholarAccount> {
        val cleanName = name.trim()
        val cleanEmail = email.trim().lowercase()
        val cleanStudentId = studentId.trim()
        val cleanUni = university.trim().ifBlank { "Yanshan University (燕山大学)" }
        val cleanDept = department.trim().ifBlank { "School of Information Science and Engineering" }
        val cleanDegree = degree.trim().ifBlank { "Bachelor of Engineering in CS & Technology" }
        val cleanNat = nationality.trim().ifBlank { "International Student" }

        if (cleanName.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your full name."))
        }
        if (cleanEmail.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your university email."))
        }
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address (e.g. name@university.edu.cn)."))
        }
        if (cleanStudentId.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your student ID."))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }
        if (password != confirmPassword) {
            return Result.failure(IllegalArgumentException("Passwords do not match. Please re-enter."))
        }
        if (hasAccount(cleanEmail, cleanStudentId)) {
            return Result.failure(IllegalArgumentException("An account with email '$cleanEmail' or ID '$cleanStudentId' already exists. Please Sign In."))
        }

        val salt = generateSalt()
        val hash = hashPassword(password, salt)

        val newProfile = UserProfileEntity(
            id = 1,
            name = cleanName,
            nationality = cleanNat,
            university = cleanUni,
            department = cleanDept,
            degree = cleanDegree,
            currentSemester = "Year 1, Semester 1 (Fall 2026)",
            expectedGraduation = "June 2028",
            researchInterests = "Computer Science, AI, Systems Engineering",
            technicalSkills = "Python, Java, Git, Linux, Data Structures",
            programmingLanguages = "Python, Java, C++",
            aiMlSkills = "Machine Learning Foundations, PyTorch Basics",
            cvSkills = "Image Processing Foundations",
            researchExperience = "International Scholar at $cleanUni",
            publications = "None yet",
            projects = "Academic Coursework & Software Projects",
            githubUrl = "https://github.com",
            certifications = "Enrolled International Student",
            chineseProficiency = "HSK 3 (Learning)",
            englishProficiency = "Fluent / Working Proficiency",
            careerGoals = "Software Engineer / AI Researcher in China or Globally",
            targetIndustries = "Technology, AI, Robotics, Software Engineering",
            targetCountries = "China, Global",
            targetCompanies = "Tech Enterprises & Research Labs",
            targetUniversities = "Top Engineering Universities",
            targetVenues = "International Journals & Conferences",
            currentAcademicTasks = "Attend curriculum classes and complete lab assignments",
            currentResearchProjects = "Undergraduate Foundation Research"
        )

        val account = ScholarAccount(
            email = cleanEmail,
            studentId = cleanStudentId,
            fullName = cleanName,
            passwordHash = hash,
            salt = salt,
            university = cleanUni,
            department = cleanDept,
            degree = cleanDegree,
            nationality = cleanNat,
            profile = newProfile
        )

        saveAccount(account)
        return Result.success(account)
    }

    fun signIn(emailOrId: String, password: String): Result<ScholarAccount> {
        val cleanInput = emailOrId.trim().lowercase()
        if (cleanInput.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your university email or student ID."))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your password."))
        }

        val account = getAllAccounts().find {
            it.email.trim().lowercase() == cleanInput ||
            it.studentId.trim().lowercase() == cleanInput
        } ?: return Result.failure(IllegalArgumentException("No account found for '$emailOrId'. Please check credentials or Sign Up."))

        val computedHash = hashPassword(password, account.salt)
        if (computedHash != account.passwordHash) {
            return Result.failure(IllegalArgumentException("Incorrect password. Please verify and try again."))
        }

        return Result.success(account)
    }

    fun getAccount(emailOrId: String): ScholarAccount? {
        val cleanInput = emailOrId.trim().lowercase()
        return getAllAccounts().find {
            it.email.trim().lowercase() == cleanInput ||
            it.studentId.trim().lowercase() == cleanInput
        }
    }

    fun updateAccountProfile(email: String, updatedProfile: UserProfileEntity): Boolean {
        val cleanEmail = email.trim().lowercase()
        val accounts = getAllAccounts().toMutableList()
        val index = accounts.indexOfFirst { it.email.trim().lowercase() == cleanEmail }
        if (index == -1) return false

        val existing = accounts[index]
        val updatedAccount = existing.copy(
            fullName = updatedProfile.name.ifBlank { existing.fullName },
            university = updatedProfile.university.ifBlank { existing.university },
            department = updatedProfile.department.ifBlank { existing.department },
            degree = updatedProfile.degree.ifBlank { existing.degree },
            nationality = updatedProfile.nationality.ifBlank { existing.nationality },
            profile = updatedProfile
        )
        accounts[index] = updatedAccount
        persistAllAccounts(accounts)
        return true
    }

    fun getAllAccounts(): List<ScholarAccount> {
        val jsonStr = prefs.getString("accounts_json", null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<ScholarAccount>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(deserializeAccount(obj))
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveAccount(account: ScholarAccount) {
        val accounts = getAllAccounts().filterNot {
            it.email.trim().lowercase() == account.email.trim().lowercase()
        }.toMutableList()
        accounts.add(account)
        persistAllAccounts(accounts)
    }

    private fun persistAllAccounts(accounts: List<ScholarAccount>) {
        val jsonArray = JSONArray()
        accounts.forEach { jsonArray.put(serializeAccount(it)) }
        prefs.edit().putString("accounts_json", jsonArray.toString()).apply()
    }

    private fun serializeAccount(account: ScholarAccount): JSONObject {
        return JSONObject().apply {
            put("email", account.email)
            put("studentId", account.studentId)
            put("fullName", account.fullName)
            put("passwordHash", account.passwordHash)
            put("salt", account.salt)
            put("university", account.university)
            put("department", account.department)
            put("degree", account.degree)
            put("nationality", account.nationality)
            put("createdAt", account.createdAt)

            val p = account.profile
            val profileObj = JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("nationality", p.nationality)
                put("university", p.university)
                put("department", p.department)
                put("degree", p.degree)
                put("currentSemester", p.currentSemester)
                put("expectedGraduation", p.expectedGraduation)
                put("researchInterests", p.researchInterests)
                put("technicalSkills", p.technicalSkills)
                put("programmingLanguages", p.programmingLanguages)
                put("aiMlSkills", p.aiMlSkills)
                put("cvSkills", p.cvSkills)
                put("researchExperience", p.researchExperience)
                put("publications", p.publications)
                put("projects", p.projects)
                put("githubUrl", p.githubUrl)
                put("certifications", p.certifications)
                put("chineseProficiency", p.chineseProficiency)
                put("englishProficiency", p.englishProficiency)
                put("careerGoals", p.careerGoals)
                put("targetIndustries", p.targetIndustries)
                put("targetCountries", p.targetCountries)
                put("targetCompanies", p.targetCompanies)
                put("targetUniversities", p.targetUniversities)
                put("targetVenues", p.targetVenues)
                put("currentAcademicTasks", p.currentAcademicTasks)
                put("currentResearchProjects", p.currentResearchProjects)
            }
            put("profile", profileObj)
        }
    }

    private fun deserializeAccount(obj: JSONObject): ScholarAccount {
        val pObj = obj.optJSONObject("profile")
        val profile = if (pObj != null) {
            UserProfileEntity(
                id = pObj.optInt("id", 1),
                name = pObj.optString("name", obj.optString("fullName")),
                nationality = pObj.optString("nationality", obj.optString("nationality")),
                university = pObj.optString("university", obj.optString("university")),
                department = pObj.optString("department", obj.optString("department")),
                degree = pObj.optString("degree", obj.optString("degree")),
                currentSemester = pObj.optString("currentSemester", "Year 1, Semester 1"),
                expectedGraduation = pObj.optString("expectedGraduation", "June 2028"),
                researchInterests = pObj.optString("researchInterests", ""),
                technicalSkills = pObj.optString("technicalSkills", ""),
                programmingLanguages = pObj.optString("programmingLanguages", ""),
                aiMlSkills = pObj.optString("aiMlSkills", ""),
                cvSkills = pObj.optString("cvSkills", ""),
                researchExperience = pObj.optString("researchExperience", ""),
                publications = pObj.optString("publications", ""),
                projects = pObj.optString("projects", ""),
                githubUrl = pObj.optString("githubUrl", ""),
                certifications = pObj.optString("certifications", ""),
                chineseProficiency = pObj.optString("chineseProficiency", "HSK 3"),
                englishProficiency = pObj.optString("englishProficiency", "Fluent"),
                careerGoals = pObj.optString("careerGoals", ""),
                targetIndustries = pObj.optString("targetIndustries", ""),
                targetCountries = pObj.optString("targetCountries", ""),
                targetCompanies = pObj.optString("targetCompanies", ""),
                targetUniversities = pObj.optString("targetUniversities", ""),
                targetVenues = pObj.optString("targetVenues", ""),
                currentAcademicTasks = pObj.optString("currentAcademicTasks", ""),
                currentResearchProjects = pObj.optString("currentResearchProjects", "")
            )
        } else {
            UserProfileEntity(
                id = 1,
                name = obj.optString("fullName", "International Scholar"),
                nationality = obj.optString("nationality", "International Student"),
                university = obj.optString("university", "Yanshan University"),
                department = obj.optString("department", "School of Information Science"),
                degree = obj.optString("degree", "CS & Technology"),
                currentSemester = "Year 1, Semester 1",
                expectedGraduation = "June 2028",
                researchInterests = "", technicalSkills = "", programmingLanguages = "", aiMlSkills = "", cvSkills = "",
                researchExperience = "", publications = "", projects = "", githubUrl = "", certifications = "",
                chineseProficiency = "HSK 3", englishProficiency = "Fluent", careerGoals = "",
                targetIndustries = "", targetCountries = "", targetCompanies = "", targetUniversities = "",
                targetVenues = "", currentAcademicTasks = "", currentResearchProjects = ""
            )
        }

        return ScholarAccount(
            email = obj.optString("email"),
            studentId = obj.optString("studentId"),
            fullName = obj.optString("fullName"),
            passwordHash = obj.optString("passwordHash"),
            salt = obj.optString("salt"),
            university = obj.optString("university"),
            department = obj.optString("department"),
            degree = obj.optString("degree"),
            nationality = obj.optString("nationality"),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
            profile = profile
        )
    }

    private fun generateSalt(): String {
        val random = SecureRandom()
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt.toByteArray(Charsets.UTF_8))
        val digest = md.digest(password.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}
