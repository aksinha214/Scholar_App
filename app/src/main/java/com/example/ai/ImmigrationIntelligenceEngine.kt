package com.example.ai

import com.example.data.model.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Stage 6: China Student -> Graduation -> Work & Immigration Intelligence Engine
 * Strictly source-driven, privacy-preserving, non-legal-automation guidance engine.
 * Never presents AI output as confirmed legal advice.
 */
object ImmigrationIntelligenceEngine {

    data class StructuredImmigrationAnswer(
        val question: String,
        val shortAnswer: String,
        val appliesTo: String,
        val conditions: List<String>,
        val requiredDocuments: List<String>,
        val responsibleAuthority: String,
        val locationOrJurisdiction: String,
        val isNational: Boolean,
        val officialSourceTitle: String,
        val sourceOrganization: String,
        val officialUrl: String,
        val publicationDate: String,
        val dateChecked: String,
        val freshnessStatus: String, // "Verified recently" or "Verification may be outdated"
        val importantUncertaintyWarning: String,
        val isVerifiedSource: Boolean = true
    )

    data class JobImmigrationAnalysis(
        val jobTitle: String,
        val company: String,
        val location: String,
        val careerMatchSummary: String,
        val applicablePathwayTitle: String,
        val pathwayJurisdiction: String,
        val degreeRequirement: String,
        val experienceExemptionAvailable: Boolean,
        val workAuthorizationConsiderations: List<String>,
        val employerResponsibilities: List<String>,
        val applicantResponsibilities: List<String>,
        val verifiedOfficialSource: String,
        val disclaimer: String
    )

    data class ResearchCareerChinaBridge(
        val researchProjectTitle: String,
        val coreTechnicalAreas: List<String>,
        val targetRole: String,
        val targetCity: String,
        val industryRelevanceInChina: String,
        val highTechPolicyAlignments: List<String>,
        val visaWorkPermitCategory: String, // e.g. "Work Permit Category B (High-Tech Field) / Regional Foreign Graduate Waiver"
        val officialSourceCitation: String,
        val guidanceNotes: String
    )

    data class PostGraduationOptionInfo(
        val code: String, // A, B, C, D, E
        val title: String,
        val generalDescription: String,
        val potentialRequirements: List<String>,
        val documentsCommonlyMentioned: List<String>,
        val responsibleAuthority: String,
        val locationSpecificConditions: String,
        val officialSource: String,
        val dateChecked: String,
        val notes: String
    )

    data class WorkAuthorizationWorkflowStep(
        val stepNumber: Int,
        val stageName: String,
        val description: String,
        val responsibleParty: String, // "Student/Applicant", "Employer", "University", "Exit-Entry Bureau", "MOHRSS/SAFEA"
        val documentsRequired: List<String>,
        val estimatedTimeline: String,
        val officialSource: String
    )

    data class ExpirationAudit(
        val type: String, // "Passport", "Residence Permit"
        val expirationDate: String,
        val daysRemaining: Long?,
        val urgencyLevel: String, // "Normal", "Warning", "Critical", "Unknown"
        val recommendation: String,
        val label: String = "Personal reminder"
    )

    // Curated official knowledge base for questions
    private val verifiedQnAKnowledge = listOf(
        StructuredImmigrationAnswer(
            question = "Can international students work off-campus or take internships in China?",
            shortAnswer = "International students are not permitted to engage in unrestricted off-campus employment. However, formal off-campus internships may be allowed if approved in advance by the university and endorsed by the local Public Security Exit-Entry Administration.",
            appliesTo = "Full-time international students holding a valid Study (学习) residence permit in China.",
            conditions = listOf(
                "Must be enrolled in good academic standing at an accredited Chinese higher education institution.",
                "The internship must be related to the student's major or degree curriculum.",
                "Must receive written consent and verification from the university international student office.",
                "Must apply to the Exit-Entry Administration to have 'Internship' (勤工助学/实习) annotated on the residence permit BEFORE starting.",
                "Work cannot exceed specified university internship duration or change locations without re-approval."
            ),
            requiredDocuments = listOf(
                "Valid passport and Study residence permit",
                "University consent letter / Internship certification form",
                "Internship agreement from the hosting enterprise / organization",
                "Accommodation registration form (境外人员住宿登记表)"
            ),
            responsibleAuthority = "University International Student Office & Local Municipal PSB Exit-Entry Administration",
            locationOrJurisdiction = "National (Ministry of Education, Ministry of Foreign Affairs, Ministry of Public Security Decree No. 42, Art. 30)",
            isNational = true,
            officialSourceTitle = "Administrative Measures for the Enrollment and Cultivation of International Students (学校招收和培养国际学生管理办法)",
            sourceOrganization = "Ministry of Education & Ministry of Public Security (教育部、公安部)",
            officialUrl = "http://www.moe.gov.cn/srcsite/A02/s5911/moe_621/201705/t20170516_304735.html",
            publicationDate = "2017-03-20",
            dateChecked = "Oct 2026",
            freshnessStatus = "Verified recently",
            importantUncertaintyWarning = "Municipal exit-entry administrations execute detailed local procedures. Off-campus work without the official residence permit endorsement constitutes illegal employment under Article 43 of the Exit-Entry Administration Law."
        ),
        StructuredImmigrationAnswer(
            question = "Can a master's graduate directly work in Shanghai without two years of prior work experience?",
            shortAnswer = "Yes, according to Shanghai municipal foreign talent guidelines, outstanding foreign graduates obtaining a master's degree or higher from designated high-level universities can apply directly for a Foreigner's Work Permit without the customary two-year work experience requirement.",
            appliesTo = "Foreign graduates with a Master's degree or above from high-level Chinese universities (e.g. 'Double First-Class' institutions) or world top-500 universities, employed by eligible Shanghai enterprises.",
            conditions = listOf(
                "Obtained Master's degree or doctoral degree.",
                "Employed by an enterprise registered and operating in Shanghai (preferably in Lin-gang Special Area, Zhangjiang Hi-Tech Park, or certified high-tech fields).",
                "Remuneration meets local prevailing foreign talent thresholds.",
                "Clean criminal record and healthy physical status."
            ),
            requiredDocuments = listOf(
                "Degree certificate and official graduation certificate",
                "Complete academic transcript",
                "Legalized/verified Non-criminal record certificate",
                "Foreigner physical examination report (issued by Travel Healthcare Center)",
                "Signed labor contract (minimum 1-year duration)",
                "Employer business license and enterprise registration on SAFEA system"
            ),
            responsibleAuthority = "Shanghai Municipal Science and Technology Commission / Shanghai Administration of Foreign Experts Affairs (上海市外国专家局)",
            locationOrJurisdiction = "Shanghai-specific (Does NOT apply universally to other cities)",
            isNational = false,
            officialSourceTitle = "Notice on Implementation Measures for Facilitating Foreign Talent Employment & Innovation in Shanghai (关于进一步支持外籍人才在沪创新创业的若干措施)",
            sourceOrganization = "Shanghai Municipal Science and Technology Commission",
            officialUrl = "http://stcsm.sh.gov.cn",
            publicationDate = "2020-04-10",
            dateChecked = "Oct 2026",
            freshnessStatus = "Verified recently",
            importantUncertaintyWarning = "This is a Shanghai-specific municipal policy. It cannot be applied to other provinces or cities. Employer qualification must be verified directly on the Foreigner Work Permit Service System."
        ),
        StructuredImmigrationAnswer(
            question = "What are the rules regarding 24-hour accommodation registration for foreigners in China?",
            shortAnswer = "Under Article 39 of the PRC Exit and Entry Administration Law, foreigners residing or staying in domiciles other than hotels must register their accommodation with the local public security organ (police station) within 24 hours of arrival or moving.",
            appliesTo = "All foreign passport holders residing or staying outside of commercial hotels in China.",
            conditions = listOf(
                "Must be completed within 24 hours of moving into off-campus housing or returning from abroad.",
                "If moving to a new address or city, registration must be updated within 24 hours at the new jurisdiction police station.",
                "If passport is renewed or residence permit is updated, accommodation registration must be refreshed.",
                "Many major cities (e.g. Shanghai, Beijing, Shenzhen) now provide online police self-declaration portals; in other locations, in-person registration at the local police station (派出所) is required."
            ),
            requiredDocuments = listOf(
                "Original passport and valid visa/residence permit",
                "Lease agreement (租房合同)",
                "Copy of landlord's ID card and property ownership certificate (房产证复印件)",
                "Landlord contact information"
            ),
            responsibleAuthority = "Local Police Station (派出所) / Exit-Entry Administration Bureau",
            locationOrJurisdiction = "National (PRC Exit and Entry Administration Law, Art. 39)",
            isNational = true,
            officialSourceTitle = "Exit and Entry Administration Law of the People's Republic of China (中华人民共和国出境入境管理法)",
            sourceOrganization = "National Immigration Administration (国家移民管理局)",
            officialUrl = "https://www.nia.gov.cn/n741440/n741542/c1212876/content.html",
            publicationDate = "2012-06-30",
            dateChecked = "Oct 2026",
            freshnessStatus = "Verified recently",
            importantUncertaintyWarning = "Failure to register within 24 hours may result in an official warning or a fine of up to RMB 2,000, and may adversely affect future residence permit renewals."
        ),
        StructuredImmigrationAnswer(
            question = "How does the China Foreigner Work Permit Tier System (Category A, B, C) operate?",
            shortAnswer = "China implements a unified Foreigner Work Permit classification dividing applicants into Category A (High-end Talent), Category B (Professional Talent), and Category C (Other Foreign Personnel). Classification is evaluated either by direct qualification or via an 85/60 point matrix.",
            appliesTo = "All foreign nationals seeking official employment authorization across mainland China.",
            conditions = listOf(
                "Category A (Score >= 85 or direct talent criteria): Top scientists, international leadership, high earners (>6x local average wage), or national talent plan recipients. Benefits include green channels and no age limit.",
                "Category B (Score >= 60 or direct qualifications): Professional foreign talent, including Master's degree holders matching high-tech roles or Bachelor's with 2 years of professional experience.",
                "Category C: Seasonal, temporary, or service personnel under national quotas.",
                "Points evaluate: Salary, educational background, relevant work experience, annual working time, Chinese language proficiency (HSK), age, and regional development encouragement points."
            ),
            requiredDocuments = listOf(
                "Online application through Foreigner Work Permit Management Service System",
                "Highest degree certificate authenticated by Chinese embassy/consulate or CSCSE",
                "Legalized certificate of no criminal conviction (valid 6 months)",
                "Medical examination report from designated healthcare center",
                "Signed labor contract or appointment letter",
                "Resume / CV detailing academic and professional timeline"
            ),
            responsibleAuthority = "Ministry of Human Resources and Social Security (MOHRSS) & SAFEA (科技部/国家外国专家局)",
            locationOrJurisdiction = "National",
            isNational = true,
            officialSourceTitle = "Evaluation Criteria for Foreigners Employed in China (Trial) (外国人来华工作分类标准(试行))",
            sourceOrganization = "Ministry of Human Resources and Social Security & SAFEA",
            officialUrl = "http://www.mohrss.gov.cn",
            publicationDate = "2017-03-28",
            dateChecked = "Oct 2026",
            freshnessStatus = "Verified recently",
            importantUncertaintyWarning = "Points calculations and document legalization criteria are strictly examined. The employer must be legally incorporated and qualified to hire foreign personnel."
        ),
        StructuredImmigrationAnswer(
            question = "What should an international student in Qinhuangdao / Hebei do when graduating from Yanshan University?",
            shortAnswer = "Graduating students must complete YSU university departure clearance, obtain degree/graduation credentials, and determine their post-graduation pathway prior to the expiration of their study residence permit. If remaining for job transition, an application for a stay permit or new status must be processed through Qinhuangdao PSB Exit-Entry Administration.",
            appliesTo = "Graduating international students of Yanshan University (燕山大学) and higher education institutions in Qinhuangdao, Hebei Province.",
            conditions = listOf(
                "Study residence permit is tied to active student enrollment; upon university graduation reporting, the study status terminates.",
                "Must apply for residence permit extension or conversion at least 30 days before expiration.",
                "If employment is secured outside Qinhuangdao (e.g. Shanghai, Beijing), the destination employer initiates the Work Permit Notification Letter, and the student updates residence status accordingly.",
                "If departing China upon graduation, student must exit before residence permit expiry or apply for a 30-day humanitarian/stay certificate (停留证件) if additional time is needed for departure logistics."
            ),
            requiredDocuments = listOf(
                "Valid passport and original Study residence permit",
                "YSU Graduation Certificate and Degree Certificate",
                "Yanshan University Clearance Form (离校手续单) / Certificate of Completion",
                "Qinhuangdao temporary residence registration slip (境外人员住宿登记表)",
                "Two standard passport photos (blue/white background)"
            ),
            responsibleAuthority = "Qinhuangdao Municipal Public Security Bureau Exit-Entry Administration (秦皇岛市公安局出入境管理支队) & YSU College of International Exchange",
            locationOrJurisdiction = "Qinhuangdao, Hebei Province",
            isNational = false,
            officialSourceTitle = "Yanshan University International Student Status & Exit-Entry Guide / Hebei PSB Guidelines",
            sourceOrganization = "YSU College of International Exchange & Qinhuangdao PSB",
            officialUrl = "http://sie.ysu.edu.cn",
            publicationDate = "2024-03-01",
            dateChecked = "Oct 2026",
            freshnessStatus = "Verified recently",
            importantUncertaintyWarning = "Exact document requirements and turnaround times should be confirmed in person with the Qinhuangdao Exit-Entry Service Hall (Haigang District) and the YSU International Office coordinator."
        )
    )

    fun answerImmigrationQuestion(query: String): StructuredImmigrationAnswer {
        val qLower = query.lowercase()
        val matched = verifiedQnAKnowledge.firstOrNull { item ->
            val titleLower = item.question.lowercase()
            when {
                qLower.contains("intern") || qLower.contains("part-time") || qLower.contains("work-study") ->
                    titleLower.contains("internship") || titleLower.contains("off-campus")
                qLower.contains("shanghai") || qLower.contains("2-year") || qLower.contains("two year") ->
                    titleLower.contains("shanghai")
                qLower.contains("hotel") || qLower.contains("24") || qLower.contains("police") || qLower.contains("registration") || qLower.contains("accommodation") ->
                    titleLower.contains("24-hour")
                qLower.contains("point") || qLower.contains("tier") || qLower.contains("category a") || qLower.contains("category b") || qLower.contains("work permit") ->
                    titleLower.contains("tier")
                qLower.contains("qinhuangdao") || qLower.contains("ysu") || qLower.contains("yanshan") || qLower.contains("hebei") ->
                    titleLower.contains("qinhuangdao")
                else -> false
            }
        }

        return matched ?: StructuredImmigrationAnswer(
            question = query,
            shortAnswer = "Official source not verified for this specific question. Please confirm directly with the relevant municipal Exit-Entry Administration, university international student office, or prospective employer.",
            appliesTo = "International students and foreign graduates in China",
            conditions = listOf(
                "Immigration, residence permit, and work authorization procedures depend strictly on official local policies and individual applicant credentials.",
                "Do not rely on unofficial third-party forums or unverified assumptions.",
                "Always verify whether policy guidelines are national or municipal-specific."
            ),
            requiredDocuments = listOf(
                "Valid Passport",
                "Current Visa / Residence Permit",
                "Official Degree / Enrollment Certificates"
            ),
            responsibleAuthority = "National Immigration Administration / Local Exit-Entry Administration",
            locationOrJurisdiction = "Jurisdiction requires confirmation",
            isNational = true,
            officialSourceTitle = "National Immigration Administration 12367 Official Service Platform",
            sourceOrganization = "National Immigration Administration (国家移民管理局)",
            officialUrl = "https://en.nia.gov.cn",
            publicationDate = "Publication/update date not available.",
            dateChecked = "Oct 2026",
            freshnessStatus = "Verification may be outdated",
            importantUncertaintyWarning = "This query did not match an indexed official source record. Consult the 12367 hotline (+86-12367) or your university advisor for authoritative guidance.",
            isVerifiedSource = false
        )
    }

    /**
     * Connects Stage 4 Career Assistant job analysis with Stage 6 China Work Authorization
     */
    fun analyzeJobImmigrationFit(
        job: JobPostingEntity,
        profile: ImmigrationProfileEntity?
    ): JobImmigrationAnalysis {
        val city = job.location.split("·", "-", " ").firstOrNull()?.trim() ?: "National"
        val degree = profile?.degreeLevel ?: "Not provided"

        val (pathway, jurisdiction, exemption, source, considerations) = when {
            city.contains("Shanghai", ignoreCase = true) || job.location.contains("上海") -> {
                Tuple5(
                    "Shanghai Direct Employment Pathway for Outstanding Foreign Graduates",
                    "Shanghai-specific",
                    true,
                    "Shanghai Municipal Science and Technology Commission / SAFEA (http://stcsm.sh.gov.cn)",
                    listOf(
                        "Master's degree from recognized Chinese 'Double First-Class' university or world top 500 allows waiver of the 2-year work experience rule.",
                        "Employer must be registered in Shanghai and qualified to sponsor Foreigner Work Permits on the SAFEA system.",
                        "Job role (${job.jobTitle}) should align with applicant's Master's major (Computer Science/Engineering).",
                        "Work permit Category B threshold requires minimum local salary benchmark."
                    )
                )
            }
            city.contains("Beijing", ignoreCase = true) || job.location.contains("北京") -> {
                Tuple5(
                    "Zhongguancun International Student Direct Employment & Innovation Pathway",
                    "Beijing-specific",
                    true,
                    "Beijing Municipal Science & Technology Commission (http://kw.beijing.gov.cn)",
                    listOf(
                        "Applicable to enterprises registered within the Zhongguancun Science Park demonstration zone.",
                        "Graduates with Master's degrees from accredited universities can obtain direct work authorization without 2 years prior experience.",
                        "Requires enterprise recommendation and tax/social contribution compliance."
                    )
                )
            }
            city.contains("Shenzhen", ignoreCase = true) || city.contains("Guangzhou", ignoreCase = true) || job.location.contains("广东") -> {
                Tuple5(
                    "Greater Bay Area Foreign Talent Work Permit Facilitation",
                    "Guangdong-specific",
                    true,
                    "Guangdong Provincial Science and Technology Department (http://gdstc.gd.gov.cn)",
                    listOf(
                        "Favorable evaluation for STEM / AI / Computer Science graduates joining high-tech innovation entities.",
                        "Work Permit Category A / B fast-track available for certified tech employers.",
                        "Requires employer sponsorship and local labor contract."
                    )
                )
            }
            city.contains("Qinhuangdao", ignoreCase = true) || job.location.contains("河北") -> {
                Tuple5(
                    "Hebei Province Foreign Talent Employment Guidelines",
                    "Hebei / Qinhuangdao",
                    false,
                    "Hebei Provincial Department of Science and Technology (http://kjt.hebei.gov.cn)",
                    listOf(
                        "Local high-tech enterprises can sponsor foreign graduate employment following provincial SAFEA verification.",
                        "Standard Category B points evaluation applies (Degree + Salary + Language + Age).",
                        "Coordination with Qinhuangdao Exit-Entry Bureau required for work-type residence permit conversion."
                    )
                )
            }
            else -> {
                Tuple5(
                    "National Foreigner Work Permit Tier System (Category B Professional Talent)",
                    "National Policy",
                    false,
                    "Ministry of Human Resources and Social Security (http://www.mohrss.gov.cn)",
                    listOf(
                        "Standard national policy normally requires Bachelor's + 2 years of relevant experience, OR a Master's degree from a Chinese university with approved foreign graduate policy.",
                        "Applicant must reach at least 60 points on the comprehensive evaluation matrix.",
                        "Confirm with destination city Human Resources Bureau whether a local graduate waiver applies."
                    )
                )
            }
        }

        return JobImmigrationAnalysis(
            jobTitle = job.jobTitle,
            company = job.company,
            location = job.location,
            careerMatchSummary = "Role in ${job.industry} aligned with CS Scholar profile (${degree}).",
            applicablePathwayTitle = pathway,
            pathwayJurisdiction = jurisdiction,
            degreeRequirement = "Master's degree or higher strongly advantageous for experience waiver.",
            experienceExemptionAvailable = exemption,
            workAuthorizationConsiderations = considerations,
            employerResponsibilities = listOf(
                "Verify enterprise registration on Foreigner Work Permit Service System.",
                "Issue formal employment contract or appointment offer meeting legal wage standards.",
                "Submit online application for 'Notification Letter of Foreigner's Work Permit' (外国人工作许可通知).",
                "Assist applicant in scheduling local Exit-Entry Bureau appointment after permit approval."
            ),
            applicantResponsibilities = listOf(
                "Maintain valid study residence status and legal presence throughout application.",
                "Provide authenticated highest degree certificate and transcripts.",
                "Provide valid medical examination report and clean criminal record check.",
                "Obtain University departure clearance and graduation certificates."
            ),
            verifiedOfficialSource = source,
            disclaimer = "This analysis is an informational synthesis based on stored official policies. It does NOT guarantee individual eligibility or work authorization approval. Always confirm with the employer's HR and the destination municipal Exit-Entry Administration."
        )
    }

    /**
     * Bridges Research Lab -> Career Assistant -> China Immigration/Work Pathway
     */
    fun buildResearchCareerChinaBridge(
        researchProject: ResearchProjectEntity?,
        careerProfile: CareerProfileEntity?,
        immigrationProfile: ImmigrationProfileEntity?
    ): ResearchCareerChinaBridge {
        val projTitle = researchProject?.title ?: "Multi-Modal Adverse Weather Perception (CS Scholar Lab)"
        val techAreas = listOf("Computer Vision", "LiDAR Point Cloud", "Edge Computing & TensorRT", "PyTorch & Deep Learning")
        val targetRole = careerProfile?.targetJobTitles?.split(",")?.firstOrNull()?.trim() ?: "Autonomous Driving / AI Algorithm Engineer"
        val targetCity = immigrationProfile?.targetEmploymentCity ?: "Shanghai"

        val alignmentNotes = listOf(
            "Adverse weather sensor perception & autonomous driving are listed as strategic encouragement fields in China's National Science & Technology Roadmap.",
            "High-tech enterprise employment in AI algorithm R&D qualifies for Foreigner Work Permit Category B bonus points under 'Encouraged Regional Industry'.",
            "Shanghai Lin-gang and Zhangjiang tech hubs offer expedited 5-working-day review for artificial intelligence R&D talents.",
            "Published research papers and patent disclosures in computer vision can be included as professional achievement evidence in SAFEA applications."
        )

        return ResearchCareerChinaBridge(
            researchProjectTitle = projTitle,
            coreTechnicalAreas = techAreas,
            targetRole = targetRole,
            targetCity = targetCity,
            industryRelevanceInChina = "High demand in Chinese smart electric vehicle (EV), robotics, and industrial automation sectors.",
            highTechPolicyAlignments = alignmentNotes,
            visaWorkPermitCategory = "Category B (Foreign Professional Talent) with high-tech industry enhancement / Shanghai Graduate Direct Waiver",
            officialSourceCitation = "MOHRSS Foreigner Work Classification Standard & Shanghai Municipal S&T Commission Notice (2020-04-10)",
            guidanceNotes = "Keep clean copies of your conference papers, code repository links, and advisor recommendation letters ready for the employer's HR to upload into the Foreigner Work Permit portal."
        )
    }

    /**
     * Informational comparison of Post-Graduation Pathways (Unranked)
     */
    fun getPostGraduationOptions(): List<PostGraduationOptionInfo> {
        return listOf(
            PostGraduationOptionInfo(
                code = "Option A",
                title = "Departure from China upon Graduation",
                generalDescription = "Conclude degree program, complete university departure procedures, and depart mainland China prior to residence permit expiration.",
                potentialRequirements = listOf(
                    "University clearance form (离校手续表) signed by all departments.",
                    "Settle campus accounts, library loans, and dormitory checkout.",
                    "If residence permit expires shortly before flight, apply for 30-day humanitarian/stay visa (停留证件)."
                ),
                documentsCommonlyMentioned = listOf(
                    "Passport and valid Study residence permit",
                    "Degree & Graduation certificates",
                    "Departure flight ticket reservation",
                    "University completion certificate"
                ),
                responsibleAuthority = "Local Exit-Entry Administration & University International Student Office",
                locationSpecificConditions = "Standard procedure nationwide across all universities.",
                officialSource = "PRC Exit and Entry Administration Law, Chapter 4",
                dateChecked = "Oct 2026",
                notes = "Do not overstay. Overstaying incurs fines of RMB 500 per day up to RMB 10,000 and administrative detention."
            ),
            PostGraduationOptionInfo(
                code = "Option B",
                title = "Direct Employment in China (Work Residence Permit)",
                generalDescription = "Transition from student status to foreign employee status via employer sponsorship, Foreigner Work Permit approval, and conversion to Work-type Residence Permit (工作类居留许可).",
                potentialRequirements = listOf(
                    "Secured job offer from a qualified employer in China.",
                    "Master's degree or higher from Chinese university (under city direct-employment policies) OR 2 years relevant experience.",
                    "Passing physical examination and legalized no-criminal-record certificate.",
                    "Employer initiates Foreigner's Work Permit Notification Letter."
                ),
                documentsCommonlyMentioned = listOf(
                    "Signed labor contract / employment agreement",
                    "Original Master's degree & graduation certificates",
                    "Official academic transcript",
                    "Foreigner Physical Examination Report (valid 6 months)",
                    "Legalized certificate of no criminal record",
                    "Valid passport and current study residence permit"
                ),
                responsibleAuthority = "Municipal Science & Technology Commission (Work Permit) & Exit-Entry Administration (Residence Permit)",
                locationSpecificConditions = "Criteria vary significantly by city: Shanghai, Beijing, Shenzhen, and Hainan possess explicit direct graduate employment pathways.",
                officialSource = "SAFEA / MOHRSS Foreigner Work Permit System & Municipal HR Regulations",
                dateChecked = "Oct 2026",
                notes = "Employment without an official work permit is illegal under Article 43 of the Exit-Entry Administration Law."
            ),
            PostGraduationOptionInfo(
                code = "Option C",
                title = "Innovation & Entrepreneurship Residence (创业/创新类签证/居留)",
                generalDescription = "Obtain an Entrepreneurship/Innovation residence permit (加注'创业'的居留证件) to establish a technology enterprise, join an accredited high-tech incubator, or develop intellectual property.",
                potentialRequirements = listOf(
                    "Graduated with Master's degree or above from high-level Chinese university.",
                    "Viable business proposal or incubation agreement with recognized national/municipal technology park (e.g. Zhongguancun, Shanghai Zhangjiang).",
                    "University recommendation letter supporting entrepreneurship."
                ),
                documentsCommonlyMentioned = listOf(
                    "Graduation and degree certificates",
                    "Incubation incubator agreement or company incorporation documents",
                    "Business plan / technology patent documentation",
                    "University recommendation for entrepreneurial endeavors"
                ),
                responsibleAuthority = "Municipal Exit-Entry Administration & Science & Technology Park Committee",
                locationSpecificConditions = "Primarily operational in tier-1 tech centers (Beijing Zhongguancun, Shanghai, Shenzhen, Hangzhou).",
                officialSource = "National Immigration Administration 12 Policies Supporting Innovation (2019)",
                dateChecked = "Oct 2026",
                notes = "Permit typically valid for 1-2 years; upon company incorporation, converts to standard employer work permit."
            ),
            PostGraduationOptionInfo(
                code = "Option D",
                title = "Further Academic Study (Ph.D. / Postdoctoral)",
                generalDescription = "Continue higher education by enrolling in a Ph.D. program or postdoctoral research position at a Chinese university or research institute.",
                potentialRequirements = listOf(
                    "Admission offer into an accredited doctoral program.",
                    "Updated JW201/JW202 form issued by the university.",
                    "Extend or transfer study residence permit before existing permit expires."
                ),
                documentsCommonlyMentioned = listOf(
                    "Official university admission notice",
                    "JW202 / JW201 visa application form",
                    "Master's degree certificate and transcript",
                    "Current residence permit and passport"
                ),
                responsibleAuthority = "Destination University International Office & Local Exit-Entry Administration",
                locationSpecificConditions = "Applies across all accredited higher education institutions in China.",
                officialSource = "Ministry of Education International Student Guidelines",
                dateChecked = "Oct 2026",
                notes = "Residence permit must be renewed or transferred prior to current expiration date."
            ),
            PostGraduationOptionInfo(
                code = "Option E",
                title = "Other Legally Available Residence Categories (Stay Permit / S2)",
                generalDescription = "Apply for a short-term Stay Permit (停留证件) or family visit status where specific legal prerequisites exist.",
                potentialRequirements = listOf(
                    "Valid reason for temporary stay, such as concluding legal/financial matters, humanitarian reasons, or family visiting.",
                    "Proof of sufficient financial means and accommodation."
                ),
                documentsCommonlyMentioned = listOf(
                    "Passport and current visa/permit",
                    "Proof of explanation letter detailing purpose of stay",
                    "Police accommodation registration slip"
                ),
                responsibleAuthority = "Local Exit-Entry Administration",
                locationSpecificConditions = "Discretionary approval by local Exit-Entry Bureau.",
                officialSource = "PRC Exit and Entry Administration Law, Article 36",
                dateChecked = "Oct 2026",
                notes = "Stay certificates do NOT authorize employment or long-term study."
            )
        )
    }

    /**
     * Employer Work Permit Checklist breakdown by responsible party
     */
    fun getWorkPermitWorkflowSteps(): List<WorkAuthorizationWorkflowStep> {
        return listOf(
            WorkAuthorizationWorkflowStep(
                stepNumber = 1,
                stageName = "Job Offer & Contract Signing",
                description = "Employer and candidate agree on terms and sign official employment contract or appointment agreement meeting statutory wage regulations.",
                responsibleParty = "Student/Applicant & Employer",
                documentsRequired = listOf("Employment Contract / Tripartite Agreement", "Job Description"),
                estimatedTimeline = "1 - 2 weeks",
                officialSource = "PRC Labor Contract Law"
            ),
            WorkAuthorizationWorkflowStep(
                stepNumber = 2,
                stageName = "Candidate Document Authentication",
                description = "Candidate collects and authenticates highest degree, certificate of no criminal conviction, and designated foreigner physical examination.",
                responsibleParty = "Student/Applicant",
                documentsRequired = listOf("Degree Certificate", "No Criminal Record", "Physical Exam Form", "Passport Copy"),
                estimatedTimeline = "2 - 4 weeks",
                officialSource = "SAFEA Document Guidelines"
            ),
            WorkAuthorizationWorkflowStep(
                stepNumber = 3,
                stageName = "Notification Letter of Foreigner's Work Permit",
                description = "Employer submits online application through the Ministry of Human Resources and Social Security / SAFEA portal. Administration conducts preliminary and substantive verification.",
                responsibleParty = "Employer & SAFEA / S&T Commission",
                documentsRequired = listOf("Enterprise Business License", "Online Application Form", "All candidate authenticated credentials"),
                estimatedTimeline = "10 - 15 working days",
                officialSource = "Foreigner Work Permit Management Service System"
            ),
            WorkAuthorizationWorkflowStep(
                stepNumber = 4,
                stageName = "Work Permit Card Issuance",
                description = "Upon arrival or in-country status adjustment, original documents are verified in person, and the biometric Foreigner Work Permit card is issued.",
                responsibleParty = "Employer & Candidate at Local S&T Bureau",
                documentsRequired = listOf("Original Passport", "Original Degree", "Original Non-Criminal Record"),
                estimatedTimeline = "5 - 7 working days",
                officialSource = "SAFEA Municipal Service Window"
            ),
            WorkAuthorizationWorkflowStep(
                stepNumber = 5,
                stageName = "Work-Type Residence Permit Application",
                description = "Candidate applies to the Municipal PSB Exit-Entry Administration to convert study residence permit to a Work-type Residence Permit (工作类居留许可).",
                responsibleParty = "Candidate & Local Exit-Entry Administration",
                documentsRequired = listOf("Valid Passport", "Foreigner Work Permit Card", "Accommodation Registration", "Application Form"),
                estimatedTimeline = "7 - 15 working days",
                officialSource = "National Immigration Administration"
            )
        )
    }

    /**
     * Audits expiration dates for Passport and Residence Permit
     */
    fun auditExpiration(
        dateStr: String,
        type: String
    ): ExpirationAudit {
        if (dateStr.isBlank() || dateStr.equals("Not provided", ignoreCase = true)) {
            return ExpirationAudit(
                type = type,
                expirationDate = "Not provided",
                daysRemaining = null,
                urgencyLevel = "Unknown",
                recommendation = "Provide $type expiration date to enable personal renewal reminders."
            )
        }

        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val target = sdf.parse(dateStr)
            val now = Date()
            if (target != null) {
                val diffMs = target.time - now.time
                val days = diffMs / (1000 * 60 * 60 * 24)
                val (urgency, recommendation) = when {
                    days < 0 -> "Critical" to "$type has EXPIRED! Consult Exit-Entry Administration immediately to avoid illegal overstay."
                    days < 30 -> "Critical" to "Less than 30 days remaining! Official residence permit renewals require at least 30 days prior application."
                    days < 90 -> "Warning" to "$days days remaining. Prepare required documents (registration, health check, degree certificates) for renewal."
                    days < 180 -> "Warning" to "$days days remaining. If planning international travel, ensure passport validity exceeds 6 months."
                    else -> "Normal" to "$days days remaining. Currently valid and in good standing."
                }
                ExpirationAudit(
                    type = type,
                    expirationDate = dateStr,
                    daysRemaining = days,
                    urgencyLevel = urgency,
                    recommendation = recommendation
                )
            } else {
                ExpirationAudit(
                    type = type,
                    expirationDate = dateStr,
                    daysRemaining = null,
                    urgencyLevel = "Unknown",
                    recommendation = "Format should be YYYY-MM-DD (e.g. 2027-07-15)."
                )
            }
        } catch (_: Exception) {
            ExpirationAudit(
                type = type,
                expirationDate = dateStr,
                daysRemaining = null,
                urgencyLevel = "Unknown",
                recommendation = "Could not parse date. Use YYYY-MM-DD."
            )
        }
    }

    // Helper tuple
    private data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
}
