package com.example.ai

import com.example.data.model.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Stage 5: Chinese Language Coach Intelligence Engine
 * Provides grounded pedagogical algorithms, curated HSK syllabus data,
 * realistic China living phrases, university & research terminology,
 * spaced repetition scheduling, and document-based vocabulary extraction.
 */
object ChineseCoachEngine {

    // ========================================================
    // 1. HSK LEARNING SYSTEM & CURATED SYLLABUS DATA
    // ========================================================

    data class HskLevelData(
        val level: String,
        val officialTitle: String,
        val verifiedVocabularyCount: Int,
        val description: String,
        val targetCapability: String,
        val grammarFocus: List<String>,
        val sampleReading: String,
        val sampleReadingPinyin: String,
        val sampleReadingEnglish: String,
        val sampleListeningPrompt: String,
        val writingFocus: String,
        val disclaimer: String = "Curated HSK Syllabus Data (Standard Levels 1-6)"
    )

    fun getOfficialHskLevels(): List<HskLevelData> {
        return listOf(
            HskLevelData(
                level = "HSK 1",
                officialTitle = "Level 1: Basic Greetings & Daily Survival",
                verifiedVocabularyCount = 150,
                description = "Understand and use simple Chinese phrases to meet basic communication needs.",
                targetCapability = "Introduce yourself, count, ask basic prices, tell time and simple greetings.",
                grammarFocus = listOf("是 (shì) identity sentences", "有/没有 (yǒu/méiyǒu) existence", "Questions with 吗 (ma) and 呢 (ne)", "Basic numbers and measure word 个 (gè)"),
                sampleReading = "你好！我是燕山大学的国际学生。很高兴认识你。",
                sampleReadingPinyin = "Nǐ hǎo! Wǒ shì Yānshān Dàxué de guójì xuésheng. Hěn gāoxìng rènshi nǐ.",
                sampleReadingEnglish = "Hello! I am an international student at Yanshan University. Very pleased to meet you.",
                sampleListeningPrompt = "请问，学校食堂在哪里？(Excuse me, where is the campus canteen?)",
                writingFocus = "Basic single-component characters: 一, 二, 三, 人, 大, 中, 国, 日, 月, 水, 火"
            ),
            HskLevelData(
                level = "HSK 2",
                officialTitle = "Level 2: Simple Daily Routine & Campus Navigation",
                verifiedVocabularyCount = 300,
                description = "Communicate in simple, routine tasks requiring direct exchange of information on familiar matters.",
                targetCapability = "Order food in the canteen, take public buses/taxis in Qinhuangdao, discuss daily schedule.",
                grammarFocus = listOf("Past aspect marker 了 (le)", "Modal verbs: 会 (huì), 能 (néng), 可以 (kěyǐ)", "Comparative sentence 比 (bǐ)", "Directions and location words: 在 (zài), 往 (wǎng)"),
                sampleReading = "今天下午两点，我们在留学生公寓一楼大厅集合，然后去超市买东西。",
                sampleReadingPinyin = "Jīntiān xiàwǔ liǎng diǎn, wǒmen zài liúxuéshēng gōngyù yī lóu dàtīng jíhé, ránhòu qù chāoshì mǎi dōngxi.",
                sampleReadingEnglish = "At 2 PM this afternoon, we assemble in the international student dorm lobby, then go to the supermarket.",
                sampleListeningPrompt = "师傅，去秦皇岛火车站需要多少时间？(Driver, how long does it take to get to Qinhuangdao Railway Station?)",
                writingFocus = "Radicals and common compounds: 吃, 喝, 走, 跑, 车, 店, 门, 课"
            ),
            HskLevelData(
                level = "HSK 3",
                officialTitle = "Level 3: Everyday Life, Study & Travel Fluency",
                verifiedVocabularyCount = 600,
                description = "Complete basic communication in life, studies, and travel in China.",
                targetCapability = "Discuss course assignments, open a Chinese bank account, register accommodation, travel independently.",
                grammarFocus = listOf("把 (bǎ) disposal structure", "被 (bèi) passive construction", "Conjunctions: 虽然...但是..., 如果...就...", "Resultative complements: 听懂, 看见, 做完"),
                sampleReading = "请同学们按时完成本周的编程作业。如果有疑问，可以在周四下午答疑时间到办公室找我。",
                sampleReadingPinyin = "Qǐng tóngxuémen ànshí wánchéng běn zhōu de biānchéng zuòyè. Rúguǒ yǒu yíwèn, kěyǐ zài zhōusì xiàwǔ dáyí shíjiān dào bàngōngshì zhǎo wǒ.",
                sampleReadingEnglish = "Please complete this week's programming assignment on time. If you have questions, visit my office during Thursday afternoon Q&A.",
                sampleListeningPrompt = "我的校园卡丢失了，请问补办需要带什么证件？(My campus card is lost. What documents do I need to bring for replacement?)",
                writingFocus = "Compound sentences and short paragraphs (80-100 characters)."
            ),
            HskLevelData(
                level = "HSK 4",
                officialTitle = "Level 4: Intermediate Campus, Academic & Technical Fluency",
                verifiedVocabularyCount = 1200,
                description = "Discuss a comparatively wide range of topics and converse fluently with native Chinese speakers.",
                targetCapability = "Attend technical university lectures, participate in computer science seminars, write formal emails to advisors.",
                grammarFocus = listOf("不仅...而且... (not only... but also)", "连...都/也... (even...)", "Complex direction complements: 拿出来, 跑过去", "Idiomatic connector: 无论...都..."),
                sampleReading = "随着人工智能与计算机视觉技术的发展，点云语义分割在自动驾驶环境感知中发挥着越来越重要的作用。",
                sampleReadingPinyin = "Suízhe réngōng zhìnéng yǔ jìsuànjī shìjué jìshù de fāzhǎn, diǎnyún yǔyì fēn'gē zài zìdòng jiǎishǐ huánjìng gǎnzhī zhōng fāhuī zhe yuèláiyuè zhòngyào de zuòyòng.",
                sampleReadingEnglish = "With the advancement of AI and computer vision, point cloud semantic segmentation plays an increasingly vital role in autonomous driving perception.",
                sampleListeningPrompt = "关于明天下午的实验室组会，请每位同学准备五分钟的项目进展汇报。(Regarding tomorrow afternoon's lab meeting, each student should prepare a 5-minute progress report.)",
                writingFocus = "Formal academic notes, structured email requests to professors, paragraph writing (150-200 characters)."
            ),
            HskLevelData(
                level = "HSK 5",
                officialTitle = "Level 5: Advanced Academic, Professional & Research Chinese",
                verifiedVocabularyCount = 2500,
                description = "Read Chinese newspapers, enjoy Chinese films, and deliver speeches in Chinese in academic and professional contexts.",
                targetCapability = "Read Chinese research papers, defend your master's thesis in Chinese, participate in technical job interviews at Chinese tech companies.",
                grammarFocus = listOf("Formal written markers: 鉴于, 由此可见, 从而, 予以, 旨在", "Rhetorical question forms: 难道, 岂能", "Adverbial subtleties: 究竟, 索性, 恰恰"),
                sampleReading = "本研究针对恶劣天气下三维激光雷达点云退化问题，提出了一种基于物理散射逆变换与注意力融合的鲁棒分割架构。实验证明其在海港多雾场景下具有显著的泛化性能。",
                sampleReadingPinyin = "Běn yánjiū zhēnduì èliè tiānqì xià sānwéi jīguāng léidá diǎnyún tuìhuà wèntí, tíchū le yī zhǒng jīyú wùlǐ sǎnsè nì biànhuàn yǔ zhùyìlì rónghé de lǔbàng fēn'gē jiàgòu. Shíyàn zhèngmíng qí zài hǎigǎng duōwù chǎngjǐng xià jùyǒu xiǎnzhù de fànhuà xìngnéng.",
                sampleReadingEnglish = "Addressing 3D LiDAR point cloud degradation in adverse weather, this research proposes a robust segmentation architecture combining physical scattering inversion and attention fusion. Experiments demonstrate superior generalization in misty seaport scenes.",
                sampleListeningPrompt = "在论文答辩过程中，专家评委重点关注了算法在极端大雾工况下的计算延时与显存消耗情况。(During thesis defense, committee experts focused on algorithm runtime latency and GPU VRAM consumption under extreme fog.)",
                writingFocus = "Academic abstracts, technical reports, cover letters, and research defense rebuttal statements."
            ),
            HskLevelData(
                level = "HSK 6",
                officialTitle = "Level 6: Mastery & Native-Level Professional Communication",
                verifiedVocabularyCount = 5000,
                description = "Easily understand what you read and hear, and express yourself smoothly in written and spoken Chinese.",
                targetCapability = "Deliver keynote presentations, negotiate tech contracts, publish in Chinese academic journals, full professional autonomy.",
                grammarFocus = listOf("Classical Chinese residual grammar in formal texts (以...为..., 所谓..., 乃至)", "Advanced rhetorical balance and parallel structures (四字成语 / 骈俪用法)"),
                sampleReading = "在深入剖析深度神经网络内部表示机制的过程中，我们不仅需探究经验风险最小化的收敛性，更应建立鲁棒泛化上界的理论保证，从而为安全攸关的自主智能体系统奠定坚实基石。",
                sampleReadingPinyin = "Zài shēnrù pōuxī shēndù shénjīng wǎngluò nèibù biǎoshì jīzhì de guòchéng zhōng, wǒmen bùjǐn xū tànjiū jīngyàn fēngxiǎn zuìxiǎohuà de shōuliǎnxìng, gèng yīng jiànlì lǔbàng fànhuà shàngjiè de lǐlùn bǎozhèng, cóng'ér wèi ānquán yōuguān de zìzhǔ zhìnéngtǐ xìtǒng diàndìng jiānshí jīshí.",
                sampleReadingEnglish = "In dissecting internal deep network representations, we must not only investigate empirical risk convergence, but also establish theoretical bounds for robust generalization, laying a solid cornerstone for safety-critical autonomous agent systems.",
                sampleListeningPrompt = "评委对您提出的理论证明提出了质疑，您如何从凸优化对偶性的角度予以回应？(The reviewer questioned your theoretical proof; how do you respond from the perspective of convex optimization duality?)",
                writingFocus = "High-level scientific discourse, research paper sections, debate and persuasive argumentation."
            )
        )
    }

    // ========================================================
    // 2. DAILY STUDY PLAN GENERATOR
    // ========================================================

    data class GeneratedStudyPlan(
        val dailyMinutes: Int,
        val daysPerWeek: Int,
        val targetLevel: String,
        val targetDate: String,
        val weeklyMinutes: Int,
        val sessions: List<ChineseStudySessionEntity>,
        val breakdownSummary: String
    )

    fun generateDailyStudyPlan(
        availableMinutes: Int,
        daysPerWeek: Int,
        preferredTime: String,
        currentLevel: String,
        targetLevel: String,
        targetDate: String,
        userEmail: String = "alexei.chen@ysu.edu.cn"
    ): GeneratedStudyPlan {
        val safeMinutes = availableMinutes.coerceIn(15, 120)
        val safeDays = daysPerWeek.coerceIn(2, 7)
        val weeklyTotal = safeMinutes * safeDays

        val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday").take(safeDays)
        val sessions = mutableListOf<ChineseStudySessionEntity>()

        // Module allocation template based on daily minutes:
        // Vocabulary (25%), Grammar (20%), Listening (15%), Speaking (15%), Reading (15%), Review (10%)
        val vocabMins = (safeMinutes * 0.25).toInt().coerceAtLeast(5)
        val grammarMins = (safeMinutes * 0.20).toInt().coerceAtLeast(5)
        val listeningMins = (safeMinutes * 0.15).toInt().coerceAtLeast(3)
        val speakingMins = (safeMinutes * 0.15).toInt().coerceAtLeast(3)
        val readingMins = (safeMinutes * 0.15).toInt().coerceAtLeast(3)
        val reviewMins = (safeMinutes - (vocabMins + grammarMins + listeningMins + speakingMins + readingMins)).coerceAtLeast(2)

        daysOfWeek.forEachIndexed { index, day ->
            when (index % 4) {
                0 -> {
                    sessions.add(
                        ChineseStudySessionEntity(
                            userEmail = userEmail,
                            dayOfWeek = day,
                            timeSlot = preferredTime,
                            moduleType = "Vocabulary & SRS",
                            title = "HSK & CS Technical Vocabulary ($vocabMins min)",
                            description = "Review spaced repetition queue and learn 10 new campus/technical terms.",
                            durationMinutes = vocabMins
                        )
                    )
                    sessions.add(
                        ChineseStudySessionEntity(
                            userEmail = userEmail,
                            dayOfWeek = day,
                            timeSlot = preferredTime,
                            moduleType = "Grammar",
                            title = "Core Grammar Pattern ($grammarMins min)",
                            description = "Practice target syntax structures (把字句, 随着...的发展).",
                            durationMinutes = grammarMins
                        )
                    )
                }
                1 -> {
                    sessions.add(
                        ChineseStudySessionEntity(
                            userEmail = userEmail,
                            dayOfWeek = day,
                            timeSlot = preferredTime,
                            moduleType = "Listening",
                            title = "Campus & Lecture Listening ($listeningMins min)",
                            description = "Practice comprehension on canteen, lab meetings, and administrative notices.",
                            durationMinutes = listeningMins
                        )
                    )
                    sessions.add(
                        ChineseStudySessionEntity(
                            userEmail = userEmail,
                            dayOfWeek = day,
                            timeSlot = preferredTime,
                            moduleType = "Speaking",
                            title = "Spoken Dialogue & Tone Drill ($speakingMins min)",
                            description = "Pronounce key sentences and speech-to-text comparison.",
                            durationMinutes = speakingMins
                        )
                    )
                }
                2 -> {
                    sessions.add(
                        ChineseStudySessionEntity(
                            userEmail = userEmail,
                            dayOfWeek = day,
                            timeSlot = preferredTime,
                            moduleType = "Reading",
                            title = "Academic Paper & Notice Reading ($readingMins min)",
                            description = "Read authentic Yanshan University announcement or paper abstract.",
                            durationMinutes = readingMins
                        )
                    )
                    sessions.add(
                        ChineseStudySessionEntity(
                            userEmail = userEmail,
                            dayOfWeek = day,
                            timeSlot = preferredTime,
                            moduleType = "Writing",
                            title = "Formal Email & Notes Writing (${safeMinutes - readingMins} min)",
                            description = "Draft respectful message to supervisor or lab coordinator.",
                            durationMinutes = safeMinutes - readingMins
                        )
                    )
                }
                3 -> {
                    sessions.add(
                        ChineseStudySessionEntity(
                            userEmail = userEmail,
                            dayOfWeek = day,
                            timeSlot = preferredTime,
                            moduleType = "Review",
                            title = "Spaced Repetition & Weakness Review ($reviewMins min)",
                            description = "Clear all 'Due Today' flashcards and practice troublesome characters.",
                            durationMinutes = reviewMins
                        )
                    )
                    sessions.add(
                        ChineseStudySessionEntity(
                            userEmail = userEmail,
                            dayOfWeek = day,
                            timeSlot = preferredTime,
                            moduleType = "Conversation",
                            title = "AI Scenario Conversation (${safeMinutes - reviewMins} min)",
                            description = "Interactive roleplay: ordering food, office hours, or tech interview.",
                            durationMinutes = safeMinutes - reviewMins
                        )
                    )
                }
            }
        }

        val breakdown = "$safeMinutes min/day across $safeDays days ($weeklyTotal min/week). Targeted from '$currentLevel' to '$targetLevel' by $targetDate."

        return GeneratedStudyPlan(
            dailyMinutes = safeMinutes,
            daysPerWeek = safeDays,
            targetLevel = targetLevel,
            targetDate = targetDate,
            weeklyMinutes = weeklyTotal,
            sessions = sessions,
            breakdownSummary = breakdown
        )
    }

    // ========================================================
    // 3. SPACED REPETITION ALGORITHM (Interval SM-2 variant)
    // ========================================================

    data class SrsResult(
        val nextReviewDate: String,
        val newIntervalDays: Int,
        val newEaseFactor: Float,
        val newReviewStatus: String,
        val newReviewCount: Int,
        val newCorrectCount: Int,
        val newIncorrectCount: Int
    )

    fun calculateSpacedRepetition(
        currentReviewCount: Int,
        currentInterval: Int,
        currentEase: Float,
        isCorrect: Boolean,
        difficulty: String = "Medium"
    ): SrsResult {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        var newInterval: Int
        var newEase = currentEase
        var newStatus: String
        val newReviewCount = currentReviewCount + 1
        var newCorrect = 0
        var newIncorrect = 0

        if (isCorrect) {
            newCorrect = 1
            // Adjust ease factor
            newEase = when (difficulty.uppercase()) {
                "EASY" -> (currentEase + 0.15f).coerceAtMost(3.0f)
                "HARD" -> (currentEase - 0.15f).coerceAtLeast(1.3f)
                else -> currentEase
            }

            newInterval = when (currentReviewCount) {
                0 -> 1
                1 -> 3
                2 -> 7
                3 -> 14
                4 -> 30
                else -> (currentInterval * newEase).toInt().coerceAtLeast(currentInterval + 7)
            }

            newStatus = if (newInterval >= 21) "Mastered" else "Upcoming"
        } else {
            newIncorrect = 1
            // Failed recall
            newInterval = 1
            newEase = (currentEase - 0.20f).coerceAtLeast(1.3f)
            newStatus = "Due"
        }

        calendar.add(Calendar.DAY_OF_YEAR, newInterval)
        val nextDateStr = dateFormat.format(calendar.time)

        return SrsResult(
            nextReviewDate = nextDateStr,
            newIntervalDays = newInterval,
            newEaseFactor = newEase,
            newReviewStatus = newStatus,
            newReviewCount = newReviewCount,
            newCorrectCount = newCorrect,
            newIncorrectCount = newIncorrect
        )
    }

    // ========================================================
    // 4. CHINESE FROM REAL LIFE IN CHINA (16 PRACTICAL CATEGORIES)
    // ========================================================

    data class PracticalPhrase(
        val category: String,
        val hanzi: String,
        val pinyin: String,
        val english: String,
        val usageExplanation: String,
        val formality: String = "Standard"
    )

    fun getRealLifePhrases(): List<PracticalPhrase> {
        return listOf(
            // 1. University
            PracticalPhrase(
                category = "University",
                hanzi = "请问国际教育学院办公室在几楼？",
                pinyin = "Qǐngwèn guójì jiàoyù xuéyuàn bàngōngshì zài jǐ lóu?",
                english = "Excuse me, on which floor is the College of International Education office?",
                usageExplanation = "Used when navigating campus administrative buildings to submit visa or enrollment papers."
            ),
            PracticalPhrase(
                category = "University",
                hanzi = "老师，我想办理在读证明和成绩单。",
                pinyin = "Lǎoshī, wǒ xiǎng bànlǐ zàidú zhèngmíng hé chéngjìdān.",
                english = "Teacher, I would like to apply for an enrollment certificate and official transcript.",
                usageExplanation = "Standard polite request to university registrar or student affairs staff."
            ),

            // 2. Classroom
            PracticalPhrase(
                category = "Classroom",
                hanzi = "老师，PPT 可以发到课程微信群里吗？",
                pinyin = "Lǎoshī, PPT kěyǐ fā dào kèchéng wēixìn qún lǐ ma?",
                english = "Professor, could the lecture slides be shared in the course WeChat group?",
                usageExplanation = "Common, natural question asked at the end of Chinese lectures."
            ),
            PracticalPhrase(
                category = "Classroom",
                hanzi = "不好意思，刚才那个算法推导我没有听懂，可以再讲一遍吗？",
                pinyin = "Bù hǎoyìsi, gāngcái nàge suànfǎ tuīdǎo wǒ méiyǒu tīng dǒng, kěyǐ zài jiǎng yí biàn ma?",
                english = "Excuse me, I didn't quite understand that algorithm derivation just now. Could you explain it once more?",
                usageExplanation = "Polite and constructive classroom clarification question."
            ),

            // 3. Dormitory
            PracticalPhrase(
                category = "Dormitory",
                hanzi = "阿姨，我们宿舍的水管好像漏水了，能找师傅维修一下吗？",
                pinyin = "Āyí, wǒmen sùshè de shuǐguǎn hǎoxiàng lòushuǐ le, néng zhǎo shīfu wéixiū yíxià ma?",
                english = "Ayi (dorm manager), our dorm water pipe seems to be leaking. Could you arrange a technician to repair it?",
                usageExplanation = "Dorm managers are respectfully addressed as 阿姨 (Āyí) or 宿管老师 (Sùguǎn lǎoshī)."
            ),
            PracticalPhrase(
                category = "Dormitory",
                hanzi = "校园一卡通热水怎么充值？",
                pinyin = "Xiàoyuán yīkǎtōng rèshuǐ zěnme chōngzhí?",
                english = "How do I top up hot water credits on my campus card?",
                usageExplanation = "Essential campus card utility phrase for campus showers."
            ),

            // 4. Restaurant
            PracticalPhrase(
                category = "Restaurant",
                hanzi = "师傅，来一份宫保鸡丁盖饭，不要放香菜，少放辣。",
                pinyin = "Shīfu, lái yí fèn gōngbǎo jīdīng gài fàn, bú yào fàng xiāngcài, shǎo fàng là.",
                english = "Master chef, please give me one Kung Pao Chicken over rice, no cilantro, less spicy.",
                usageExplanation = "Ordering at university canteens or casual local diners."
            ),
            PracticalPhrase(
                category = "Restaurant",
                hanzi = "服务员，买单！微信还是支付宝？",
                pinyin = "Fúwùyuán, mǎidān! Wēixìn háishi Zhīfùbǎo?",
                english = "Waiter, the bill please! WeChat or Alipay?",
                usageExplanation = "Standard method of asking for the QR code to pay at restaurants."
            ),

            // 5. Shopping
            PracticalPhrase(
                category = "Shopping",
                hanzi = "请问这个多少钱？支持扫码支付吗？",
                pinyin = "Qǐngwèn zhège duōshao qián? Zhīchí sǎomǎ zhìfù ma?",
                english = "Excuse me, how much is this? Do you accept mobile QR payment?",
                usageExplanation = "Basic shopping inquiry at local markets or campus stores."
            ),
            PracticalPhrase(
                category = "Shopping",
                hanzi = "麻烦帮我拿一个大号的包装袋，谢谢。",
                pinyin = "Máfan bāng wǒ ná yí ge dà hào de bāozhuāng dài, xièxie.",
                english = "Could you please give me a large shopping bag, thank you.",
                usageExplanation = "Polite request using 麻烦 (máfan) at grocery stores."
            ),

            // 6. Transportation
            PracticalPhrase(
                category = "Transportation",
                hanzi = "师傅，我去秦皇岛站北广场，请打表走。",
                pinyin = "Shīfu, wǒ qù Qínhuángdǎo zhàn běi guǎngchǎng, qǐng dǎ biǎo zǒu.",
                english = "Driver, I am going to Qinhuangdao Station North Square, please turn on the meter.",
                usageExplanation = "Taking a standard street taxi. Alternatively, Didi (滴滴) is app-based."
            ),
            PracticalPhrase(
                category = "Transportation",
                hanzi = "请问去燕山大学东校区坐几路公交车？",
                pinyin = "Qǐngwèn qù Yānshān Dàxué dōng xiàoqū zuò jǐ lù gōngjiāochē?",
                english = "Excuse me, which bus route goes to Yanshan University East Campus?",
                usageExplanation = "Asking for public bus directions in northern Chinese cities."
            ),

            // 7. Bank
            PracticalPhrase(
                category = "Bank",
                hanzi = "你好，我是留学生，想开立一个借记卡并开通网银短信服务。",
                pinyin = "Nǐ hǎo, wǒ shì liúxuéshēng, xiǎng kāilì yí ge jièjìkǎ bìng kāitōng wǎngyín duǎnxìn fúwù.",
                english = "Hello, I am an international student. I would like to open a debit card and enable SMS banking notifications.",
                usageExplanation = "SMS service is critical for binding Chinese bank cards to WeChat and Alipay."
            ),
            PracticalPhrase(
                category = "Bank",
                hanzi = "请问接收境外汇款需要提供哪些银行代码和信息？",
                pinyin = "Qǐngwèn jiēshōu jìngwài huìkuǎn xūyào tígōng nǎxiē yínháng dàimǎ hé xìnxī?",
                english = "What SWIFT codes and bank branch information are required to receive international wire transfers?",
                usageExplanation = "Inquiry at Bank of China (中国银行) or ICBC (工行) counter."
            ),

            // 8. Hospital
            PracticalPhrase(
                category = "Hospital",
                hanzi = "护士，我想挂发热门诊的号。请问在哪里建卡？",
                pinyin = "Hùshi, wǒ xiǎng guà fārè ménzhěn de hào. Qǐngwèn zài nǎlǐ jiàn kǎ?",
                english = "Nurse, I need to register for the fever clinic. Where do I create a patient card?",
                usageExplanation = "Registration (挂号 guàhào) and patient card (建卡 jiànkǎ) are the first steps at Chinese public hospitals."
            ),
            PracticalPhrase(
                category = "Hospital",
                hanzi = "医生，我从昨天晚上开始头痛并且发烧三十八度五。",
                pinyin = "Yīshēng, wǒ cóng zuótiān wǎnshang kāishǐ tóutòng bìngqiě fāshāo sānshíbā dù wǔ.",
                english = "Doctor, I have had a headache and a fever of 38.5°C since yesterday evening.",
                usageExplanation = "Factual symptom reporting to an attending doctor."
            ),

            // 9. Police / Administration
            PracticalPhrase(
                category = "Police/administration",
                hanzi = "你好，我是校外租房的留学生，来办理二十四小时境外人员住宿登记。",
                pinyin = "Nǐ hǎo, wǒ shì xiàowài zūfáng de liúxuéshēng, lái bànlǐ èrshísì xiǎoshí jìngwài rényuán zhùsù dēngjì.",
                english = "Hello, I am an international student renting off-campus, here to complete the 24-hour temporary residence registration.",
                usageExplanation = "Mandatory legal requirement at the local police substation (派出所 pàichūsuǒ)."
            ),
            PracticalPhrase(
                category = "Police/administration",
                hanzi = "这是我的护照、租房合同以及房东的身份证复印件。",
                pinyin = "Zhè shì wǒ de hùzhào, zūfáng hétong yǐjí fángdōng de shēnfènzhèng fùyìnjiàn.",
                english = "Here is my passport, lease agreement, and a copy of the landlord's national ID card.",
                usageExplanation = "Standard documents required by entry-exit police administration."
            ),

            // 10. Phone / SIM
            PracticalPhrase(
                category = "Phone/SIM",
                hanzi = "我想办理一张校园流量套餐的电话卡，用护照实名认证。",
                pinyin = "Wǒ xiǎng bànlǐ yì zhāng xiàoyuán liúliàng tàocān de diànhuàkǎ, yòng hùzhào shímíng rènzhèng.",
                english = "I would like to apply for a campus data plan SIM card, authenticated with my passport real-name verification.",
                usageExplanation = "China Mobile (中国移动) or China Unicom (中国联通) telecom counter phrase."
            ),
            PracticalPhrase(
                category = "Phone/SIM",
                hanzi = "请问这个套餐每个月包含多少通用流量和定向流量？",
                pinyin = "Qǐngwèn zhège tàocān měi ge yuè bāohán duōshao tōngyòng liúliàng hé dìngxiàng liúliàng?",
                english = "How much general data and app-specific data are included in this monthly plan?",
                usageExplanation = "Clarifies Chinese carrier data breakdown (通用 = any app, 定向 = specific apps like Bilibili/Tencent)."
            ),

            // 11. Renting
            PracticalPhrase(
                category = "Renting",
                hanzi = "房东您好，请问这套房子离燕山大学东校区有多远？押金怎么交？",
                pinyin = "Fángdōng nín hǎo, qǐngwèn zhè tào fángzi lí Yānshān Dàxué dōng xiàoqū yǒu duō yuǎn? Yājīn zěnme jiāo?",
                english = "Hello landlord, how far is this apartment from YSU East Campus? How is the deposit structured?",
                usageExplanation = "Common deposit model in China is 押一付三 (1 month deposit, 3 months prepaid rent)."
            ),
            PracticalPhrase(
                category = "Renting",
                hanzi = "房屋租赁合同中是否包含物业费、暖气费和宽带费用？",
                pinyin = "Fángwū zūlìn hétong zhōng shìfǒu bāohán wùyè fèi, nuǎnqì fèi hé kuāndài fèiyòng?",
                english = "Does the lease agreement include property management, winter heating, and broadband internet fees?",
                usageExplanation = "Northern China heating (暖气 nuǎnqì) is an important seasonal cost in Qinhuangdao."
            ),

            // 12. Daily Conversation
            PracticalPhrase(
                category = "Daily conversation",
                hanzi = "周末有空吗？要不要一起去海边栈道散步？",
                pinyin = "Zhōumò yǒu kòng ma? Yào bu yào yìqǐ qù hǎibiān zhàndào sànbù?",
                english = "Are you free this weekend? Want to take a walk along the coastal boardwalk together?",
                usageExplanation = "Casual social invitation suitable for classmates in coastal Qinhuangdao."
            ),
            PracticalPhrase(
                category = "Daily conversation",
                hanzi = "今天风挺大的，出门记得多穿一件外套。",
                pinyin = "Jīntiān fēng tǐng dà de, chūmén jìde duō chuān yí jiàn wàitào.",
                english = "It's quite windy today, remember to put on an extra jacket before heading out.",
                usageExplanation = "Warm, natural everyday small talk among peers."
            ),

            // 13. Friendship & Social
            PracticalPhrase(
                category = "Friendship/social situations",
                hanzi = "非常感谢你这段时间对我的帮助！哪天有空我请你喝奶茶。",
                pinyin = "Fēicháng gǎnxiè nǐ zhè duàn shíjiān duì wǒ de bāngzhù! Nǎ tiān yǒu kòng wǒ qǐng nǐ hē nǎichá.",
                english = "Thank you so much for helping me during this time! When you're free, boba milk tea is on me.",
                usageExplanation = "Standard, friendly, relaxed expression of gratitude among Chinese university students."
            ),
            PracticalPhrase(
                category = "Friendship/social situations",
                hanzi = "加一下微信吧，我扫你的二维码。",
                pinyin = "Jiā yíxià wēixìn ba, wǒ sǎo nǐ de èrwéimǎ.",
                english = "Let's connect on WeChat, I'll scan your QR code.",
                usageExplanation = "Everyday social exchange to stay in touch."
            ),

            // 14. Academic Research
            PracticalPhrase(
                category = "Academic research",
                hanzi = "我们在测试集上评估了算法，mIoU 指标比基线模型提升了三点二个百分点。",
                pinyin = "Wǒmen zài cèshìjí shàng pínggū le suànfǎ, mIoU zhǐbiāo bǐ jīxiàn móxíng tíshēng le sān diǎn èr ge bǎifēndiǎn.",
                english = "We evaluated the algorithm on the test set, achieving a 3.2 percentage point gain in mIoU over the baseline.",
                usageExplanation = "Precise, professional technical phrasing for lab group meetings."
            ),
            PracticalPhrase(
                category = "Academic research",
                hanzi = "消融实验结果表明，可变形交叉注意力模块对细小目标的分割精度至关重要。",
                pinyin = "Xiāoróng shíyàn jiéguǒ biǎomíng, kě biànxíng jiāochā zhùyìlì mókuài duì xìxiǎo mùbiāo de fēn'gē jīngdù zhìguān zhòngyào.",
                english = "Ablation experimental results indicate that the deformable cross-attention module is crucial for small object segmentation accuracy.",
                usageExplanation = "Academic paper presentation and discussion phrasing."
            ),

            // 15. Job Interview
            PracticalPhrase(
                category = "Job interview",
                hanzi = "各位面试官好，我主要研究三维点云感知与计算机视觉算法，熟悉 PyTorch 和 C++ 部署。",
                pinyin = "Gèwèi miànshìguān hǎo, wǒ zhǔyào yánjiū sānwéi diǎnyún gǎnzhī yǔ jìsuànjī shìjué suànfǎ, shúxī PyTorch hé C++ bùshǔ.",
                english = "Hello interviewers, my research focuses on 3D point cloud perception and computer vision algorithms, and I am proficient in PyTorch and C++ deployment.",
                usageExplanation = "Strong, concise opening pitch in technical engineering interviews."
            ),
            PracticalPhrase(
                category = "Job interview",
                hanzi = "请问贵团队在自动驾驶车载端侧模型量化与 TensorRT 加速方面有哪些技术实践？",
                pinyin = "Qǐngwèn guì tuánduì zài zìdòng jiǎishǐ chēzài duāncè móxíng liànghuà yǔ TensorRT jiāsù fāngmiàn yǒu nǎxiē jìshù shíjiàn?",
                english = "May I ask what technical practices your team uses for vehicle onboard model quantization and TensorRT acceleration?",
                usageExplanation = "Smart, authoritative reverse question for candidates to ask interviewers."
            ),

            // 16. Workplace
            PracticalPhrase(
                category = "Workplace",
                hanzi = "我已经把最新的 PR 代码提交到了开发分支，麻烦相关同事帮忙 Code Review。",
                pinyin = "Wǒ yǐjīng bǎ zuìxīn de PR dàimǎ tíjiāo dào le kāifā fēnzhī, máfan xiāngguān tóngshì bāngmáng Code Review.",
                english = "I have submitted the latest PR code to the develop branch; could relevant colleagues please help with the Code Review?",
                usageExplanation = "Natural engineering communication inside Chinese tech firms (often mixing PR and Code Review)."
            ),
            PracticalPhrase(
                category = "Workplace",
                hanzi = "关于这个 API 接口的响应延时问题，我们明天上午开个对齐会同步一下解决方案。",
                pinyin = "Guānyú zhège API jiēkǒu de xiǎngyìng yánshí wèntí, wǒmen míngtiān shàngwǔ kāi ge duìqí huì tóngbù yíxià jiějué fāng'àn.",
                english = "Regarding the response latency issue of this API endpoint, let's hold an alignment meeting tomorrow morning to sync on the solution.",
                usageExplanation = "Authentic modern Chinese tech workplace jargon (对齐 = align, 同步 = sync)."
            )
        )
    }

    // ========================================================
    // 5. UNIVERSITY CHINESE (10 REALISTIC SCENARIOS)
    // ========================================================

    data class UniversityScenario(
        val id: String,
        val titleZh: String,
        val titleEn: String,
        val formalTarget: String,
        val keyVocabulary: List<String>,
        val dialogueSnippet: String,
        val emailTemplate: String
    )

    fun getUniversityScenarios(): List<UniversityScenario> {
        return listOf(
            UniversityScenario(
                id = "advisor_meeting",
                titleZh = "向导师汇报科研进展",
                titleEn = "Meeting with Academic Advisor",
                formalTarget = "Professor / Supervisor (张老师 / 教授)",
                keyVocabulary = listOf("进展 (progress)", "开题报告 (thesis proposal)", "实验验证 (experimental validation)", "请教 (consult respectfully)"),
                dialogueSnippet = "学生：张老师您好，上周您建议调整的点云滤波参数，我已经完成了消融实验，想向您汇报一下实验结果。\n导师：好的，把对比曲线和混淆矩阵调出来，我们一起看一下。",
                emailTemplate = "尊敬的张老师：\n您好！我是计算机学院研究生 Alexei。关于多雾海港点云去噪算法的基准对比实验已取得初步结果。不知您本周四或周五下午是否有空，学生希望向您当面汇报并请教下一步方案。祝好！\n学生：Alexei"
            ),
            UniversityScenario(
                id = "assignment_clarification",
                titleZh = "向任课教师请教课程作业",
                titleEn = "Asking About Assignments",
                formalTarget = "Course Lecturer (任课老师)",
                keyVocabulary = listOf("作业要求 (assignment requirements)", "截止日期 (deadline)", "边界条件 (boundary condition)", "提交格式 (submission format)"),
                dialogueSnippet = "学生：李老师，操作系统大作业要求实现 Copy-on-Write 机制，请问页表引用的计数器应该放在内核态还是用户态？\n老师：计数器必须维护在内核页表元数据中，防止并发竞争。",
                emailTemplate = "李老师您好：\n我是选修《操作系统内核实验》的国际学生 Alexei。在实现第 3 次实验的过程中，对锁机制的边界条件有一些疑问。已将问题细节和复现代码附在邮件附件中，恳请老师抽空指点。非常感谢！"
            ),
            UniversityScenario(
                id = "deadline_extension",
                titleZh = "礼貌申请作业/报告延期",
                titleEn = "Requesting an Extension",
                formalTarget = "Professor / Department Head",
                keyVocabulary = listOf("申请延期 (apply for extension)", "事出有因 (justified reason)", "望请见谅 (hope for understanding)", "准时提交 (submit punctually)"),
                dialogueSnippet = "学生：王老师您好，由于实验室 GPU 服务器正在停机维护，模型验证尚未跑完，我想申请将论文初稿延期三天提交，不知是否可行？\n老师：可以，但请确保在周日晚上 24:00 前将完整稿件发送到邮箱。",
                emailTemplate = "尊敬的王老师：\n您好！我是研究生 Alexei。原定于本周三提交的课程研究报告，因集群硬件维护耽误了实验收敛。为确保分析数据的严谨性，学生冒昧申请将提交时间顺延至周五中午。给老师带来的不便深感抱歉，望请老师批准！"
            ),
            UniversityScenario(
                id = "clarification_concept",
                titleZh = "课后向老师请教概念澄清",
                titleEn = "Asking for Clarification",
                formalTarget = "Lecturer (授课教师)",
                keyVocabulary = listOf("概念 (concept)", "理解有误 (misunderstanding)", "推导步骤 (derivation steps)", "请教 (consult)"),
                dialogueSnippet = "学生：陈老师打扰一下，关于今天讲的凸优化对偶性定理，我不太确定在非强凸条件下对偶间隙是否依然为零？\n老师：问得很好，在非强凸且不满足 Slater 条件时，可能存在弱对偶间隙。",
                emailTemplate = "陈老师您好：\n在复习今天课上的最大流最小割定理证明时，有一处反证法步骤理解得不够透彻。已将我的推演草稿拍照附于附件，想请教老师该推导是否成立。多谢老师解惑！"
            ),
            UniversityScenario(
                id = "classmates_collaboration",
                titleZh = "与中国同学讨论小组项目",
                titleEn = "Talking to Classmates & Team Members",
                formalTarget = "Peer / Classmate (同学)",
                keyVocabulary = listOf("分工 (task allocation)", "接口 (interface)", "联调 (integration testing)", "仓库 (git repo)"),
                dialogueSnippet = "Alexei：大家看一下前端界面和后端 API 的返回格式，我们今晚先把 Docker 镜像打包好，明天下午在三教自习室一起联调如何？\n同学：没问题，我把数据结构转换写好了，直接 push 到 dev 分支了。",
                emailTemplate = "各位组员好：\n这是我们本周软工大作业的 Sprint 任务看板。请大家在周四前完成各自模块的单元测试，周五下午两点我们在图书馆研讨室统一合代码。辛苦大家！"
            ),
            UniversityScenario(
                id = "presenting_research",
                titleZh = "在学术组会上作论文汇报",
                titleEn = "Presenting Research in Lab",
                formalTarget = "Lab Group / Research Peers",
                keyVocabulary = listOf("动机 (motivation)", "基线 (baseline)", "消融实验 (ablation)", "局限性 (limitations)"),
                dialogueSnippet = "Alexei：大家好，今天我汇报的题目是《恶劣天气下基于物理逆散射的三维点云语义分割》。我们首先看一下现有 SOTA 模型在雾天场景下的性能衰退数据……\n师兄：你的特征解耦模块参数量是多少？实时推理延时表现如何？",
                emailTemplate = "各位老师、同门好：\n本周五上午 9:30 的组会由我进行近期实验汇报，汇报幻灯片《PointFog-SAM: Adverse Weather LiDAR Segmentation》已上传至实验室内部网盘，欢迎大家批评指正！"
            ),
            UniversityScenario(
                id = "discussing_paper",
                titleZh = "与同门讨论最新文献",
                titleEn = "Discussing a Paper with Lab Mates",
                formalTarget = "Senior Lab Mates (师兄 / 师姐 / 同学)",
                keyVocabulary = listOf("创新点 (novel contribution)", "复现难度 (reproducibility)", "开源代码 (open-source code)", "启发 (inspiration)"),
                dialogueSnippet = "Alexei：师兄，你看昨天 CVPR 刚放出来的这篇 3D Gaussian Splatting 论文了吗？他们的动态场景建模思路对我们的港口监控项目很有借鉴意义。\n师兄：看了，作者开源了 CUDA 算子，我们可以先下载他们的预训练权重在海港点云上跑一下测试。",
                emailTemplate = "师兄好：\n附件是昨天在 arXiv 上看到的最新论文，其中提出的多尺度空间交叉注意力机制与我们正在设计的 backbone 非常契合。标注了核心创新点，供参考讨论！"
            ),
            UniversityScenario(
                id = "lab_communication",
                titleZh = "实验室服务器与设备借用沟通",
                titleEn = "Laboratory Communication & GPU Booking",
                formalTarget = "Lab Admin / Equipment Manager",
                keyVocabulary = listOf("算力节点 (compute node)", "显存 (VRAM)", "占用时长 (duration)", "申请借用 (apply to borrow)"),
                dialogueSnippet = "Alexei：师兄你好，我今晚需要跑一个全量点云的对比训练，大概需要占用 2 号服务器的两张 RTX 4090 十二个小时，请问晚上有其他人排队吗？\n师兄：目前晚上没人用，你挂好 nohup 后在群里发个通知就行。",
                emailTemplate = "实验室管理员好：\n计算机学院 Alexei 申请借用便携式 64 线激光雷达一台，用于本周五下午在西校区体育场进行多径散射数据标定采集。设备归还时间为周六上午 10:00 前，保证妥善爱护器材。望请登记备案。"
            ),
            UniversityScenario(
                id = "email_supervisor",
                titleZh = "给导师发正式申请/请假邮件",
                titleEn = "Emailing a Supervisor (Formal Etiquette)",
                formalTarget = "Academic Supervisor (导师)",
                keyVocabulary = listOf("呈送 (present respectfully)", "特此申请 (hereby apply)", "望予批准 (humbly request approval)", "顺祝教安 (wishing teaching wellness)"),
                dialogueSnippet = "（书面邮件礼仪，见下文范文）",
                emailTemplate = "尊敬的张导师：\n您好！\n学生 Alexei 近期因需前往秦皇岛市公安局出入境管理局办理居留许可延期手续，特向您请假半天（周二上午 8:30-12:00）。在此期间已安排同组同学代为留意服务器实验进程。办结后学生将立即返校投入科研工作。\n特此呈报，望予批准！\n\n顺祝\n教安！\n\n计算机科学与技术学院 硕士生 Alexei\n2026年10月"
            ),
            UniversityScenario(
                id = "university_administration",
                titleZh = "高校留学生办公室及教务办事",
                titleEn = "University Administration & Visa Coordination",
                formalTarget = "Administrative Staff (教务处 / 留管科老师)",
                keyVocabulary = listOf("居留许可 (residence permit)", "盖章 (stamp official seal)", "学籍证明 (student status doc)", "JW202表 (JW202 form)"),
                dialogueSnippet = "Alexei：老师您好，出入境管理局要求学校在境外人员签证延期申请表上加盖公章，请问需要携带哪些审核材料？\n老师：请带上你的学生证、护照原件以及导师签字的居留延期同意书。",
                emailTemplate = "国际教育学院留学生管理科老师好：\n我是计算机学院 2024 级留学生 Alexei，学号 202409876。因居留许可证将于下月末到期，特向学院申请出具《境外人员签证/停留许可申请函》并盖章。已将表格草案填写完毕，恳请老师审核。感谢老师的辛勤工作！"
            )
        )
    }

    // ========================================================
    // 6. RESEARCH & ACADEMIC CHINESE (COMPUTER SCIENCE & AI)
    // ========================================================

    data class ResearchChineseTerm(
        val category: String,
        val hanzi: String,
        val pinyin: String,
        val english: String,
        val technicalMeaning: String,
        val exampleUsage: String
    )

    fun getResearchChineseTerms(): List<ResearchChineseTerm> {
        return listOf(
            ResearchChineseTerm(
                category = "Computer Vision",
                hanzi = "三维点云",
                pinyin = "sān wéi diǎn yún",
                english = "3D Point Cloud",
                technicalMeaning = "A set of data points in space, typically produced by 3D scanners or LiDAR sensors, with (X, Y, Z) and intensity features.",
                exampleUsage = "我们在海港实测环境中采集了高密度三维点云数据。"
            ),
            ResearchChineseTerm(
                category = "Computer Vision",
                hanzi = "语义分割",
                pinyin = "yǔ yì fēn gē",
                english = "Semantic Segmentation",
                technicalMeaning = "Pixel-level or point-level classification assigning a semantic category label to every coordinate in the scan.",
                exampleUsage = "点云语义分割旨在识别出地面、集装箱、行人和车辆类别。"
            ),
            ResearchChineseTerm(
                category = "Computer Vision",
                hanzi = "恶劣天气恢复",
                pinyin = "è liè tiān qì huī fù",
                english = "Adverse Weather Restoration",
                technicalMeaning = "Computational techniques to remove effects of atmospheric scattering (fog, rain, snow) from sensory data.",
                exampleUsage = "针对浓雾天气导致的点云丢点现象，我们设计了物理散射退化恢复模型。"
            ),
            ResearchChineseTerm(
                category = "Artificial Intelligence",
                hanzi = "交叉注意力机制",
                pinyin = "jiāo chā zhù yì lì jī zhì",
                english = "Cross-Attention Mechanism",
                technicalMeaning = "An attention layer where Query comes from one modality or representation and Key/Value come from another.",
                exampleUsage = "通过交叉注意力机制，模型有效融合了空间几何特征与纹理上下文。"
            ),
            ResearchChineseTerm(
                category = "Artificial Intelligence",
                hanzi = "经验风险最小化",
                pinyin = "jīng yàn fēng xiǎn zuì xiǎo huà",
                english = "Empirical Risk Minimization (ERM)",
                technicalMeaning = "A principle in statistical learning theory defining the optimization objective on observed training samples.",
                exampleUsage = "过度依赖经验风险最小化往往会导致模型在未知天气域上的过拟合。"
            ),
            ResearchChineseTerm(
                category = "Machine Learning",
                hanzi = "消融实验",
                pinyin = "xiāo róng shí yàn",
                english = "Ablation Study",
                technicalMeaning = "Systematic removal of individual model components to verify their exact contribution to final performance metrics.",
                exampleUsage = "消融实验清晰地证实了散射逆映射模块带来了 2.4% 的 mIoU 增益。"
            ),
            ResearchChineseTerm(
                category = "Machine Learning",
                hanzi = "过拟合 / 欠拟合",
                pinyin = "guò nǐ hé / qiàn nǐ hé",
                english = "Overfitting / Underfitting",
                technicalMeaning = "Poor generalization due to excessively memorizing noise (overfitting) or insufficient capacity (underfitting).",
                exampleUsage = "增加权重衰减 (weight decay) 和 Dropout 有效抑制了训练后期的过拟合。"
            ),
            ResearchChineseTerm(
                category = "Programming",
                hanzi = "显存溢出",
                pinyin = "xiǎn cún yì chū",
                english = "CUDA Out of Memory (OOM)",
                technicalMeaning = "GPU memory exhausted when tensor allocations exceed available video RAM during forward/backward passes.",
                exampleUsage = "当 batch size 超过 8 时，4090 显卡会发生显存溢出，需采用梯度累积 (Gradient Accumulation)。"
            ),
            ResearchChineseTerm(
                category = "Programming",
                hanzi = "混合精度训练",
                pinyin = "hùn hé jīng dù xùn liàn",
                english = "Mixed Precision Training",
                technicalMeaning = "Training using both FP16/BF16 and FP32 to accelerate computation and cut VRAM footprint.",
                exampleUsage = "在 PyTorch 中使用 torch.cuda.amp 开启自动混合精度训练，训练吞吐提升了 40%。"
            ),
            ResearchChineseTerm(
                category = "Academic Writing",
                hanzi = "基线模型",
                pinyin = "jī xiàn mó xíng",
                english = "Baseline Model",
                technicalMeaning = "Standard representative existing methods against which a new algorithm is rigorously benchmarked.",
                exampleUsage = "我们选取了 PointNeXt 和 Cylinder3D 作为对比基线模型。"
            ),
            ResearchChineseTerm(
                category = "Academic Writing",
                hanzi = "开题报告 / 预答辩",
                pinyin = "kāi tí bào gào / yù dá biàn",
                english = "Thesis Proposal / Pre-Defense",
                technicalMeaning = "Milestone graduate checkpoints: formally defining research problem and presenting preliminary dissertation findings.",
                exampleUsage = "下周二上午教研室组织研究生开题报告评审会。"
            ),
            ResearchChineseTerm(
                category = "Software Engineering",
                hanzi = "端侧模型量化",
                pinyin = "duān cè mó xíng liàng huà",
                english = "On-Device Model Quantization",
                technicalMeaning = "Converting 32-bit floating point weights to INT8 to accelerate inference on edge devices with minimal accuracy loss.",
                exampleUsage = "使用 TensorRT 对感知算法进行 INT8 PTQ 量化，满足车载计算芯片的 30 FPS 实时性约束。"
            )
        )
    }

    // ========================================================
    // 7. DOCUMENT-BASED CHINESE EXTRACTION
    // ========================================================

    data class ExtractedChineseItem(
        val hanzi: String,
        val pinyin: String,
        val english: String,
        val contextSentence: String,
        val category: String,
        val sourceDocument: String
    )

    fun extractChineseFromDocument(
        documentContent: String,
        documentName: String,
        category: String = "Campus Document"
    ): List<ExtractedChineseItem> {
        val results = mutableListOf<ExtractedChineseItem>()
        if (documentContent.isBlank()) return results

        // Verified vocabulary bank for reliable extraction matching
        val knownTerms = listOf(
            Triple("延期", "yán qī", "postpone / extend deadline"),
            Triple("学费", "xué fèi", "tuition fee"),
            Triple("奖学金", "jiǎng xué jīn", "scholarship"),
            Triple("宿舍", "sù shè", "dormitory"),
            Triple("居留许可", "jū liú xǔ kě", "residence permit"),
            Triple("签证", "qiān zhèng", "visa"),
            Triple("考试", "kǎo shì", "examination"),
            Triple("答辩", "dá biàn", "defense"),
            Triple("导师", "dǎo shī", "supervisor / advisor"),
            Triple("开题", "kāi tí", "thesis proposal"),
            Triple("成绩单", "chéng jì dān", "transcript"),
            Triple("在读证明", "zài dú zhèng míng", "certificate of enrollment"),
            Triple("公章", "gōng zhāng", "official stamp / seal"),
            Triple("体检", "tǐ jiǎn", "health checkup"),
            Triple("医保", "yī bǎo", "medical insurance"),
            Triple("放假", "fàng jià", "vacation / holiday"),
            Triple("开学", "kāi xué", "school reopening"),
            Triple("请假", "qǐng jià", "request leave"),
            Triple("报到", "bào dào", "register / check in"),
            Triple("通知", "tōng zhī", "notice / announcement"),
            Triple("作业", "zuò yè", "assignment / homework"),
            Triple("实验室", "shí yàn shì", "laboratory"),
            Triple("组会", "zǔ huì", "lab meeting"),
            Triple("退宿", "tuì sù", "check out of dorm"),
            Triple("点云", "diǎn yún", "point cloud"),
            Triple("算法", "suàn fǎ", "algorithm"),
            Triple("模型", "mó xíng", "model"),
            Triple("训练", "xùn liàn", "training"),
            Triple("测试", "cè shì", "testing / benchmark")
        )

        val lines = documentContent.lines()
        knownTerms.forEach { (hanzi, pinyin, english) ->
            if (documentContent.contains(hanzi)) {
                val matchedLine = lines.firstOrNull { it.contains(hanzi) } ?: "$hanzi 出现在文档中。"
                results.add(
                    ExtractedChineseItem(
                        hanzi = hanzi,
                        pinyin = pinyin,
                        english = english,
                        contextSentence = matchedLine.trim().take(120),
                        category = category,
                        sourceDocument = documentName
                    )
                )
            }
        }

        return results
    }

    // ========================================================
    // 8. CHINESE <-> ENGLISH TRANSLATOR & POLITENESS PRAGMATICS
    // ========================================================

    data class TranslationResult(
        val originalText: String,
        val direction: String, // "ZH_TO_EN" or "EN_TO_ZH"
        val literalTranslation: String,
        val naturalTranslation: String,
        val pinyin: String,
        val politenessLevel: String, // "Formal (Professor/Dean)", "Polite (Colleagues/Admin)", "Casual (Friends)"
        val usageNotes: String
    )

    fun translateAndExplain(
        inputText: String,
        direction: String = "EN_TO_ZH",
        formality: String = "FORMAL"
    ): TranslationResult {
        val trimmed = inputText.trim()

        if (direction == "EN_TO_ZH") {
            // English -> Chinese
            return when {
                trimmed.contains("leave", ignoreCase = true) || trimmed.contains("sick", ignoreCase = true) -> {
                    TranslationResult(
                        originalText = trimmed,
                        direction = "EN_TO_ZH",
                        literalTranslation = "我生病了，想要申请离开学校几天。",
                        naturalTranslation = if (formality == "FORMAL")
                            "张老师您好：学生因身体突发不适，需前往医院就诊并遵医嘱休养，特向您请假两天。已安排好手头实验，请老师放心。"
                        else
                            "老师，我今天发烧不舒服，想请一天病假去医院看一下。",
                        pinyin = "Zhāng lǎoshī nín hǎo: Xuésheng yīn shēntǐ tūfā bùshì, xū qiánwǎng yīyuàn jiùzhěn...",
                        politenessLevel = if (formality == "FORMAL") "Formal (Professor / Advisor)" else "Standard Polite",
                        usageNotes = "When addressing a supervisor, avoid blunt statements like 'I am taking leave'. Always state that you have arranged ongoing work."
                    )
                }
                trimmed.contains("extension", ignoreCase = true) || trimmed.contains("delay", ignoreCase = true) -> {
                    TranslationResult(
                        originalText = trimmed,
                        direction = "EN_TO_ZH",
                        literalTranslation = "我想要更多的作业时间。",
                        naturalTranslation = if (formality == "FORMAL")
                            "尊敬的李老师：关于本期课后大作业，因近期实验基准复现遇到意外技术阻碍，学生冒昧申请将提交时间顺延两日至周五。望请老师体谅并批准！"
                        else
                            "李老师好，作业目前还差最后两个模块联调，想申请延期两天提交，不知是否可以？",
                        pinyin = "Zūnjìng de Lǐ lǎoshī: Guānyú běnqī kèhòu dà zuòyè, yīn jìnqī shíyàn...",
                        politenessLevel = if (formality == "FORMAL") "High Politeness / Formal" else "Standard Polite",
                        usageNotes = "In Chinese academic culture, stating the technical justification and expressing humility ('冒昧申请') makes requests much more receptive."
                    )
                }
                trimmed.contains("meeting", ignoreCase = true) || trimmed.contains("discuss", ignoreCase = true) -> {
                    TranslationResult(
                        originalText = trimmed,
                        direction = "EN_TO_ZH",
                        literalTranslation = "我想和你开会讨论论文。",
                        naturalTranslation = if (formality == "FORMAL")
                            "张老师您好：关于近期开题论文的实验进展，学生希望能向您当面请教。不知您本周后半周何时方便？学生可按您的时间前往办公室。"
                        else
                            "老师您好，论文开题部分遇到了一点疑问，不知您哪天有空指点一下？",
                        pinyin = "Zhāng lǎoshī nín hǎo: Guānyú jìnqī kāití lùnwén de shíyàn jìnzhǎn...",
                        politenessLevel = "Formal (Academic Etiquette)",
                        usageNotes = "Always allow the senior professor to designate the time by asking '不知您何时方便' rather than proposing a rigid meeting slot."
                    )
                }
                else -> {
                    TranslationResult(
                        originalText = trimmed,
                        direction = "EN_TO_ZH",
                        literalTranslation = "字面含义：$trimmed",
                        naturalTranslation = "老师您好，关于相关事宜特此向您请教与汇报：$trimmed",
                        pinyin = "Lǎoshī nín hǎo, guānyú xiāngguān shìyí...",
                        politenessLevel = "Formal Respectful",
                        usageNotes = "Natural translation adjusted for polite communication with university mentors and staff."
                    )
                }
            }
        } else {
            // Chinese -> English
            return TranslationResult(
                originalText = trimmed,
                direction = "ZH_TO_EN",
                literalTranslation = "Direct character-by-character translation: $trimmed",
                naturalTranslation = "Natural English equivalent with academic & professional clarity: $trimmed",
                pinyin = "Pīnyīn notation corresponding to character stream",
                politenessLevel = "Analytical Academic",
                usageNotes = "Chinese formal phrases often contain polite softening particles and honorific prefixes (您, 拜读, 冒昧, 望予)."
            )
        }
    }

    // ========================================================
    // 9. SPEECH TRANSCRIPTION COMPARISON & PRONUNCIATION GUIDANCE
    // ========================================================

    data class SpeakingEvaluation(
        val expectedPhrase: String,
        val transcribedText: String,
        val matchPercentage: Int,
        val toneGuidance: String,
        val disclaimer: String = "Speech transcription comparison (not a medical/lab acoustic analysis)"
    )

    fun evaluateSpeechTranscription(expectedPhrase: String, transcribedText: String): SpeakingEvaluation {
        if (transcribedText.isBlank()) {
            return SpeakingEvaluation(
                expectedPhrase = expectedPhrase,
                transcribedText = "(No audio transcribed)",
                matchPercentage = 0,
                toneGuidance = "Please speak clearly into the microphone or test your sentence with speech recognition."
            )
        }

        val cleanedExpected = expectedPhrase.filter { it in '\u4e00'..'\u9fa5' }
        val cleanedTranscribed = transcribedText.filter { it in '\u4e00'..'\u9fa5' }

        var matches = 0
        cleanedExpected.forEach { char ->
            if (cleanedTranscribed.contains(char)) matches++
        }

        val percentage = if (cleanedExpected.isNotEmpty()) {
            ((matches.toFloat() / cleanedExpected.length.toFloat()) * 100).toInt().coerceIn(0, 100)
        } else 80

        val guidance = when {
            percentage >= 90 -> "Excellent transcription match! Your pronunciation and articulation were clearly recognized by speech-to-text."
            percentage >= 70 -> "Good match! Watch syllable initial/final boundaries (e.g. z/c/s vs zh/ch/sh) and ensure 3rd tones dip fully."
            percentage >= 40 -> "Partial match. Speak at a steady pace and emphasize the 4th tone (falling sharply from pitch 5 to 1)."
            else -> "Low transcription match. Check that your sentence matches the target characters and pronounce each syllable distinctly."
        }

        return SpeakingEvaluation(
            expectedPhrase = expectedPhrase,
            transcribedText = transcribedText,
            matchPercentage = percentage,
            toneGuidance = guidance
        )
    }

    // ========================================================
    // 10. PRONUNCIATION & TONES GUIDE
    // ========================================================

    data class ToneInfo(
        val toneNumber: Int,
        val nameZh: String,
        val nameEn: String,
        val pitchContour: String,
        val description: String,
        val exampleSyllable: String,
        val exampleHanzi: String,
        val exampleMeaning: String = "",
        val tip: String
    )

    fun getTonesGuide(): List<ToneInfo> {
        return listOf(
            ToneInfo(
                toneNumber = 1,
                nameZh = "第一声 (阴平)",
                nameEn = "1st Tone: High Level",
                pitchContour = "55 (High & Flat)",
                description = "High, steady, sustained pitch like singing a high sustained note.",
                exampleSyllable = "mā",
                exampleHanzi = "妈 (mother)",
                tip = "Keep your throat steady and pitch high without letting it drop or rise."
            ),
            ToneInfo(
                toneNumber = 2,
                nameZh = "第二声 (阳平)",
                nameEn = "2nd Tone: Rising",
                pitchContour = "35 (Mid to High)",
                description = "Starts at a medium pitch and glides steadily upward, similar to asking 'What?!' in English.",
                exampleSyllable = "má",
                exampleHanzi = "麻 (hemp / numb)",
                tip = "Start mid-range and rise smoothly to the top of your vocal pitch."
            ),
            ToneInfo(
                toneNumber = 3,
                nameZh = "第三声 (上声)",
                nameEn = "3rd Tone: Dipping (Low & Fall-Rise)",
                pitchContour = "214 (Mid-Low -> Lowest -> Rising)",
                description = "Dips down into the vocal fry range and gently curves upward when spoken in isolation.",
                exampleSyllable = "mǎ",
                exampleHanzi = "马 (horse)",
                tip = "In continuous speech, 3rd tone is often pronounced as a 'half 3rd tone' (just low and flat 21)."
            ),
            ToneInfo(
                toneNumber = 4,
                nameZh = "第四声 (去声)",
                nameEn = "4th Tone: Falling (Sharp Drop)",
                pitchContour = "51 (High to Lowest)",
                description = "Starts at the top of your pitch and drops forcefully and decisively, like giving an urgent command ('Stop!').",
                exampleSyllable = "mà",
                exampleHanzi = "骂 (scold)",
                tip = "Drop rapidly from high to low with confidence; do not prolong the vowel."
            ),
            ToneInfo(
                toneNumber = 0,
                nameZh = "轻声",
                nameEn = "Neutral Tone: Light & Soft",
                pitchContour = "Short & Variable",
                description = "Pronounced softly, shortly, and without emphasis, taking pitch from the preceding syllable.",
                exampleSyllable = "ma",
                exampleHanzi = "吗 (question particle)",
                tip = "Relax your vocal cords; keep it about half the duration of standard syllables."
            )
        )
    }

    // ========================================================
    // 11. CHINESE CHARACTER STROKE PRACTICE ARCHITECTURE
    // ========================================================

    data class CharacterPracticeItem(
        val character: String,
        val pinyin: String,
        val meaning: String,
        val strokeCount: Int,
        val radical: String,
        val strokeOrderDescription: String,
        val exampleWords: List<String>,
        val exampleSentence: String
    )

    fun getCharacterPracticeItems(): List<CharacterPracticeItem> {
        return listOf(
            CharacterPracticeItem(
                character = "学",
                pinyin = "xué",
                meaning = "to study / learn / science",
                strokeCount = 8,
                radical = "子 (child)",
                strokeOrderDescription = "Dot, dot, right-falling dot, dot, horizontal hook, bend, curved hook, horizontal.",
                exampleWords = listOf("学生 (student)", "大学 (university)", "科学 (science)", "学术 (academic)"),
                exampleSentence = "他在燕山大学学习计算机科学与技术。"
            ),
            CharacterPracticeItem(
                character = "算",
                pinyin = "suàn",
                meaning = "to calculate / compute / reckon",
                strokeCount = 14,
                radical = "竹 (bamboo)",
                strokeOrderDescription = "Bamboo radical on top, middle 目 (eye), bottom 廾 (hands).",
                exampleWords = listOf("计算机 (computer)", "算法 (algorithm)", "计算 (compute)", "算力 (computing power)"),
                exampleSentence = "这个深度学习算法在 GPU 上计算速度非常快。"
            ),
            CharacterPracticeItem(
                character = "研",
                pinyin = "yán",
                meaning = "to grind / investigate / research",
                strokeCount = 9,
                radical = "石 (stone)",
                strokeOrderDescription = "Left 石 (stone), right 开 (open/grind).",
                exampleWords = listOf("研究 (research)", "研究生 (graduate student)", "科研 (scientific research)", "研发 (R&D)"),
                exampleSentence = "我们的科研团队主要研究恶劣天气下的视觉感知。"
            ),
            CharacterPracticeItem(
                character = "网",
                pinyin = "wǎng",
                meaning = "net / network / web",
                strokeCount = 6,
                radical = "冂 (down open box)",
                strokeOrderDescription = "Down, horizontal-bend-hook, drop, cross, drop, cross.",
                exampleWords = listOf("神经网络 (neural network)", "网络 (internet/network)", "网页 (webpage)", "网盘 (cloud drive)"),
                exampleSentence = "卷积神经网络是深度视觉处理的经典基石。"
            )
        )
    }

    // ========================================================
    // 12. WEAKNESS ANALYZER (Based strictly on stored user data)
    // ========================================================

    data class WeaknessAnalysisReport(
        val hasSufficientData: Boolean,
        val summaryStatement: String,
        val weakAreas: List<String>,
        val strongAreas: List<String>,
        val recommendations: List<String>
    )

    fun analyzeLearningWeaknesses(
        profile: ChineseLanguageProfileEntity?,
        vocabList: List<ChineseVocabularyEntity>,
        listeningExercises: List<ChineseListeningExerciseEntity>
    ): WeaknessAnalysisReport {
        if (vocabList.isEmpty() && listeningExercises.none { it.isAnswered }) {
            return WeaknessAnalysisReport(
                hasSufficientData = false,
                summaryStatement = "Not enough data yet.",
                weakAreas = emptyList(),
                strongAreas = emptyList(),
                recommendations = listOf("Complete at least 5 vocabulary reviews and 3 listening exercises to generate factual analytics.")
            )
        }

        val totalReviews = vocabList.sumOf { it.reviewCount }
        val incorrectReviews = vocabList.sumOf { it.incorrectAnswers }
        val answeredListening = listeningExercises.filter { it.isAnswered }
        val incorrectListening = answeredListening.count { !it.isCorrect }

        val weak = mutableListOf<String>()
        val strong = mutableListOf<String>()
        val recs = mutableListOf<String>()

        // Vocabulary analysis
        if (totalReviews > 0) {
            val vocabErrorRate = (incorrectReviews.toFloat() / totalReviews.toFloat()) * 100
            if (vocabErrorRate > 35) {
                weak.add("Vocabulary Recall (Error rate: ${vocabErrorRate.toInt()}%)")
                recs.add("Prioritize reviewing 'Due' flashcards daily to reinforce memory intervals.")
            } else {
                strong.add("Vocabulary Retention (Error rate: ${vocabErrorRate.toInt()}%)")
            }
        }

        // Listening analysis
        if (answeredListening.isNotEmpty()) {
            val listeningErrorRate = (incorrectListening.toFloat() / answeredListening.size.toFloat()) * 100
            if (listeningErrorRate > 30) {
                weak.add("Listening Comprehension (Incorrect: ${incorrectListening}/${answeredListening.size})")
                recs.add("Hide English translations during listening practice and focus on tone contours.")
            } else {
                strong.add("Listening Comprehension (${answeredListening.size - incorrectListening}/${answeredListening.size} correct)")
            }
        }

        // Self-confidence metrics from profile
        if (profile != null) {
            if (profile.speakingConfidence <= 2) {
                weak.add("Speaking Confidence (Self-rating: ${profile.speakingConfidence}/5)")
                recs.add("Use the Speaking Practice tool to transcribe full sentences aloud.")
            }
            if (profile.writingConfidence <= 2) {
                weak.add("Writing & Emailing Confidence (Self-rating: ${profile.writingConfidence}/5)")
                recs.add("Practice formal email templates in the University Chinese module.")
            }
            if (profile.readingConfidence >= 4) {
                strong.add("Reading Fluency (Self-rating: ${profile.readingConfidence}/5)")
            }
        }

        return WeaknessAnalysisReport(
            hasSufficientData = true,
            summaryStatement = if (weak.isEmpty()) "Consistent performance across recorded tasks."
            else "Identified ${weak.size} focus areas based on your recorded performance data.",
            weakAreas = weak,
            strongAreas = strong,
            recommendations = recs
        )
    }

    // ========================================================
    // 13. AI CHINESE TUTOR PROMPT RESPONDER
    // ========================================================

    data class TutorResponse(
        val replyContent: String,
        val suggestedFollowUps: List<String>,
        val vocabularyToSave: List<ChineseVocabularyEntity> = emptyList()
    )

    fun handleTutorCommand(
        command: String,
        profile: ChineseLanguageProfileEntity?,
        userEmail: String = "alexei.chen@ysu.edu.cn"
    ): TutorResponse {
        val lower = command.lowercase()
        return when {
            lower.contains("10") && (lower.contains("word") || lower.contains("university") || lower.contains("vocab")) -> {
                val words = listOf(
                    ChineseVocabularyEntity(userEmail = userEmail, hanzi = "开题报告", pinyin = "kāití bàogào", english = "thesis proposal", category = "University", hskLevel = "HSK 4"),
                    ChineseVocabularyEntity(userEmail = userEmail, hanzi = "导师", pinyin = "dǎoshī", english = "academic advisor", category = "University", hskLevel = "HSK 3"),
                    ChineseVocabularyEntity(userEmail = userEmail, hanzi = "答辩", pinyin = "dábiàn", english = "thesis defense", category = "University", hskLevel = "HSK 5"),
                    ChineseVocabularyEntity(userEmail = userEmail, hanzi = "校园一卡通", pinyin = "xiàoyuán yīkǎtōng", english = "campus smart card", category = "University", hskLevel = "HSK 3"),
                    ChineseVocabularyEntity(userEmail = userEmail, hanzi = "教务处", pinyin = "jiàowùchù", english = "academic affairs office", category = "University", hskLevel = "HSK 4"),
                    ChineseVocabularyEntity(userEmail = userEmail, hanzi = "在读证明", pinyin = "zàidú zhèngmíng", english = "certificate of enrollment", category = "University", hskLevel = "HSK 4"),
                    ChineseVocabularyEntity(userEmail = userEmail, hanzi = "实验室", pinyin = "shíyànshì", english = "laboratory", category = "University", hskLevel = "HSK 3"),
                    ChineseVocabularyEntity(userEmail = userEmail, hanzi = "挂号", pinyin = "guàhào", english = "register at clinic/hospital", category = "Campus Life", hskLevel = "HSK 3"),
                    ChineseVocabularyEntity(userEmail = userEmail, hanzi = "消融实验", pinyin = "xiāoróng shíyàn", english = "ablation study", category = "Research", hskLevel = "HSK 5"),
                    ChineseVocabularyEntity(userEmail = userEmail, hanzi = "居留许可", pinyin = "jūliú xǔkě", english = "residence permit", category = "Administration", hskLevel = "HSK 4")
                )
                TutorResponse(
                    replyContent = "Here are 10 essential Chinese terms for university and lab life at Yanshan University:\n\n" +
                            words.mapIndexed { i, w -> "${i + 1}. **${w.hanzi}** (${w.pinyin}) — ${w.english} [${w.hskLevel}]" }.joinToString("\n") +
                            "\n\nYou can click 'Save to Vocabulary' below to add them to your spaced-repetition deck.",
                    suggestedFollowUps = listOf("Teach me how to address my advisor in an email", "Quiz me on these 10 words", "Show example sentences"),
                    vocabularyToSave = words
                )
            }
            lower.contains("professor") || lower.contains("advisor") -> {
                TutorResponse(
                    replyContent = "When communicating with your Chinese professor or supervisor:\n\n" +
                            "1. **Salutation**: Always use `[Surname] + 老师` (e.g. `张老师您好`) or `尊敬的 [Surname] 教授`.\n" +
                            "2. **Honorific Pronoun**: Use `您` (nín) instead of `你` (nǐ).\n" +
                            "3. **Self-Reference**: Refer to yourself as `学生 Alexei` (Your student Alexei).\n" +
                            "4. **Asking for Time**: Ask `不知您本周何时方便` (Wondering when you might be free) rather than dictating a time.\n" +
                            "5. **Closing**: Use `顺祝教安！` (Wishing teaching wellness) or `祝好！`.",
                    suggestedFollowUps = listOf("Draft an email requesting a research meeting", "Draft an extension request", "Practice office-hour dialogue")
                )
            }
            lower.contains("computer science") || lower.contains("cs") || lower.contains("technical") -> {
                TutorResponse(
                    replyContent = "Core Computer Science & Deep Learning Chinese terminology:\n\n" +
                            "• **点云语义分割** (diǎnyún yǔyì fēn'gē): Point cloud semantic segmentation\n" +
                            "• **显存溢出** (xiǎncún yìchū): CUDA Out of Memory (OOM)\n" +
                            "• **注意力机制** (zhùyìlì jīzhì): Attention mechanism\n" +
                            "• **基线对比** (jīxiàn duìbǐ): Baseline comparison\n" +
                            "• **端侧量化** (duāncè liànghuà): On-device quantization\n" +
                            "• **过拟合** (guònǐhé): Overfitting",
                    suggestedFollowUps = listOf("How to explain my research problem in Chinese?", "Teach me phrases for a technical interview", "Explain CUDA terms")
                )
            }
            lower.contains("hsk") -> {
                val current = profile?.currentLevel ?: "Not assessed"
                val target = profile?.targetHskLevel ?: "HSK 5"
                TutorResponse(
                    replyContent = "HSK Preparation Advice (Current: $current → Target: $target):\n\n" +
                            "1. **Vocabulary Foundation**: HSK 4 requires 1,200 words; HSK 5 requires 2,500 words. Focus on 20 new words/day using Spaced Repetition.\n" +
                            "2. **Grammar Patterns**: Master formal connectors (`鉴于`, `从而`, `由此可见`, `不仅...而且...`).\n" +
                            "3. **Reading Speed**: Target 120-150 characters per minute for HSK 5 reading passages.\n" +
                            "4. **Writing Practice**: Practice structured 80-word compositions using given keywords.",
                    suggestedFollowUps = listOf("Open HSK 4 section", "Open HSK 5 section", "Generate daily study schedule")
                )
            }
            else -> {
                TutorResponse(
                    replyContent = "Hello! I am your AI Chinese Language Coach, calibrated for your university and CS research journey in China.\n\n" +
                            "You can ask me to:\n" +
                            "• 'Teach me 10 Chinese words for university life'\n" +
                            "• 'Practice a conversation with my professor'\n" +
                            "• 'Explain this Chinese notice'\n" +
                            "• 'Help me prepare for HSK'\n" +
                            "• 'Teach me Chinese words used in computer science'",
                    suggestedFollowUps = listOf("Teach me 10 Chinese words for university life", "Practice a conversation with my professor", "Help me prepare for HSK")
                )
            }
        }
    }
}
