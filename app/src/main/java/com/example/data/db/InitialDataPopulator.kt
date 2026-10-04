package com.example.data.db

import com.example.data.model.*

object InitialDataPopulator {
    suspend fun populate(db: AppDatabase) {
        // 1. Initial User Profile
        val initialProfile = UserProfileEntity(
            id = 1,
            name = "Alexei Chen-Kovalenko",
            nationality = "International Student (Kazakhstan / Central Asia)",
            university = "Yanshan University (燕山大学, Qinhuangdao, China)",
            department = "School of Information Science and Engineering (信息科学与工程学院)",
            degree = "Bachelor of Engineering in Computer Science and Technology (计算机科学与技术)",
            currentSemester = "Year 3, Semester 1 (Fall 2026)",
            expectedGraduation = "June 2027",
            researchInterests = "Computer Vision, 3D LiDAR Perception, Vision Transformers, Adverse Weather Sensing, Semi-Supervised Learning",
            technicalSkills = "PyTorch, OpenCV, CUDA, C++, Python, Linux (Ubuntu), Git, Docker, ROS, TensorRT",
            programmingLanguages = "Python (Expert), C/C++ (Intermediate-Advanced), Java (Intermediate), SQL, Bash",
            aiMlSkills = "Deep Learning, CNNs, Vision Transformers (ViT), Semantic Segmentation, Object Detection (YOLO/DETR), PyTorch Lightning",
            cvSkills = "Point Cloud Processing (Open3D, PointNet++), Multi-View Geometry, Optical Flow, Adverse Weather Restoration",
            researchExperience = "Undergraduate Research Fellow at Yanshan Intelligent Information Processing Lab (Supervised by Prof. Zhang Lin)",
            publications = "1 under review at ACCV 2026: 'Multi-Scale Feature Distillation for Robust Industrial Defect Inspection'; Working on CVPR 2027 submission",
            projects = "LiDAR-FogNet: 3D Point Cloud Semantic Segmentation in Maritime Coastal Fog; High-Speed Rail Fastener Defect Detection using YOLOv10",
            githubUrl = "https://github.com/alexei-ysu-cs",
            certifications = "DeepLearning.AI Deep Learning Specialization; NVIDIA DLI Fundamentals of Deep Learning; CS50x Harvard",
            chineseProficiency = "HSK 4 (Certified, Score: 254/300); Currently preparing for HSK 5 (Target: 220+ by Dec 2026)",
            englishProficiency = "Fluent / IELTS Academic 7.5 (C1 Level)",
            careerGoals = "Pursue a direct PhD or Master's at a top AI Lab (Tsinghua/ZJU/NUS) or work as an AI Algorithm / Computer Vision Engineer in China or Singapore",
            targetIndustries = "Autonomous Driving, Robotics Perception, Industrial AI Inspection, Intelligent Systems",
            targetCountries = "China, Singapore, Germany, Canada, Switzerland",
            targetCompanies = "DeepSeek, Baidu Apollo, DJI, Tencent AI Lab, Huawei Noah's Ark, SenseTime, NIO Autonomous Driving, Alibaba DAMO Academy",
            targetUniversities = "Tsinghua University (THU), Zhejiang University (ZJU), USTC, Shanghai Jiao Tong (SJTU), National University of Singapore (NUS)",
            targetVenues = "CVPR, ICCV, ECCV, NeurIPS, IEEE TPAMI, IEEE TIP",
            currentAcademicTasks = "Complete xv6 OS file system lab; Prepare Computer Vision term presentation; Submit HSK 5 registration",
            currentResearchProjects = "Point Cloud Fog-Robustness Benchmark on Qinhuangdao Port LiDAR dataset"
        )
        db.userProfileDao().insertOrUpdateProfile(initialProfile)

        // 1.5. Initial University Profile
        val uniProfile = UniversityProfileEntity(
            id = 1,
            university = "Yanshan University (燕山大学)",
            department = "School of Information Science and Engineering (信息科学与工程学院)",
            degree = "Bachelor of Engineering in CS & Technology (计算机科学与技术)",
            semester = "Year 3, Semester 1 (Fall 2026)",
            academicYear = "2026-2027",
            semesterStartDate = "2026-09-01",
            semesterEndDate = "2027-01-15",
            defaultClassDurationMinutes = 95,
            attendanceTarget = 80.0f,
            availableDailyStudyHours = 4.0f,
            preferredStudyStartTime = "18:00",
            preferredStudyEndTime = "22:00",
            sleepStartTime = "23:30",
            sleepEndTime = "07:00"
        )
        db.universityDao().insertOrUpdateUniversityProfile(uniProfile)

        // 2. Initial Courses with attendance
        val courses = listOf(
            CourseEntity(
                code = "CS301",
                name = "Advanced Algorithms & Optimization",
                professorName = "Prof. Wang Zhi (王治教授)",
                professorContact = "wangzhi@ysu.edu.cn | Office: Info Bldg 408",
                credits = 4,
                classroom = "East Campus Teaching Bldg 4-302",
                scheduleTime = "Monday & Wednesday 08:00 - 09:35",
                syllabusSummary = "Graph algorithms, Network Flow, NP-completeness, Dynamic Programming in high dimensions, Convex optimization for ML.",
                notes = "Midterm will test dynamic programming and amortized analysis proofs.",
                attendanceTarget = 80.0f,
                scheduledClasses = 32,
                attendedClasses = 28,
                absentClasses = 2,
                excusedClasses = 0,
                colorHex = "#00D2FF"
            ),
            CourseEntity(
                code = "CS305",
                name = "Computer Vision & Pattern Recognition",
                professorName = "Prof. Zhang Lin (张琳教授)",
                professorContact = "zhanglin_cv@ysu.edu.cn | Lab: AI Center 602",
                credits = 3,
                classroom = "East Campus Science Hall 5-201",
                scheduleTime = "Tuesday & Thursday 10:15 - 11:50",
                syllabusSummary = "Feature extraction, SIFT/ORB, Deep convolutional architectures, Object detection, Semantic segmentation, NeRF, 3D Vision.",
                notes = "Final project worth 40% of grade. Needs novel architecture ablation or benchmark on domain dataset.",
                attendanceTarget = 80.0f,
                scheduledClasses = 24,
                attendedClasses = 21,
                absentClasses = 1,
                excusedClasses = 1,
                colorHex = "#FFB703"
            ),
            CourseEntity(
                code = "CS308",
                name = "Operating Systems Internals & Kernel Lab",
                professorName = "Prof. Chen Gang (陈刚副教授)",
                professorContact = "chengang@ysu.edu.cn | Office: Info Bldg 312",
                credits = 4,
                classroom = "East Campus Teaching Bldg 3-101",
                scheduleTime = "Friday 09:50 - 12:15",
                syllabusSummary = "Process scheduling, virtual memory paging, xv6 kernel modifications, deadlock prevention, file system caching.",
                notes = "Labs submitted via GitLab. xv6 code cleanliness strictly graded.",
                attendanceTarget = 85.0f,
                scheduledClasses = 16,
                attendedClasses = 14,
                absentClasses = 1,
                excusedClasses = 0,
                colorHex = "#10B981"
            ),
            CourseEntity(
                code = "CH302",
                name = "Advanced Technical & Academic Chinese",
                professorName = "Teacher Li Hua (李华老师)",
                professorContact = "lihua_iec@ysu.edu.cn | Intl College 205",
                credits = 2,
                classroom = "International Exchange Center 204",
                scheduleTime = "Monday 16:00 - 17:35",
                syllabusSummary = "Academic writing in Chinese, scientific terminology, research paper abstracts, oral thesis defense etiquette.",
                notes = "Final assessment: 10-minute research proposal presentation in Chinese.",
                attendanceTarget = 75.0f,
                scheduledClasses = 16,
                attendedClasses = 15,
                absentClasses = 0,
                excusedClasses = 1,
                colorHex = "#A855F7"
            )
        )
        for (course in courses) {
            db.universityDao().insertCourse(course)
        }

        // 2.5 Timetable Classes (Monday - Friday)
        val timetable = listOf(
            TimetableClassEntity(
                courseCode = "CS301",
                courseName = "Advanced Algorithms & Optimization",
                teacher = "Prof. Wang Zhi",
                dayOfWeek = "Monday",
                startTime = "08:00",
                endTime = "09:35",
                classroom = "East Campus Bldg 4-302",
                weekRange = "Weeks 1-16",
                scheduleType = "Weekly"
            ),
            TimetableClassEntity(
                courseCode = "CH302",
                courseName = "Advanced Technical Chinese",
                teacher = "Teacher Li Hua",
                dayOfWeek = "Monday",
                startTime = "16:00",
                endTime = "17:35",
                classroom = "Intl Exchange Center 204",
                weekRange = "Weeks 1-16",
                scheduleType = "Weekly"
            ),
            TimetableClassEntity(
                courseCode = "CS305",
                courseName = "Computer Vision & Pattern Recognition",
                teacher = "Prof. Zhang Lin",
                dayOfWeek = "Tuesday",
                startTime = "10:15",
                endTime = "11:50",
                classroom = "Science Hall 5-201",
                weekRange = "Weeks 1-16",
                scheduleType = "Weekly"
            ),
            TimetableClassEntity(
                courseCode = "CS301",
                courseName = "Advanced Algorithms & Optimization",
                teacher = "Prof. Wang Zhi",
                dayOfWeek = "Wednesday",
                startTime = "08:00",
                endTime = "09:35",
                classroom = "East Campus Bldg 4-302",
                weekRange = "Weeks 1-16",
                scheduleType = "Weekly"
            ),
            TimetableClassEntity(
                courseCode = "CS305",
                courseName = "Computer Vision & Pattern Recognition",
                teacher = "Prof. Zhang Lin",
                dayOfWeek = "Thursday",
                startTime = "10:15",
                endTime = "11:50",
                classroom = "Science Hall 5-201",
                weekRange = "Weeks 1-16",
                scheduleType = "Weekly"
            ),
            TimetableClassEntity(
                courseCode = "CS308",
                courseName = "Operating Systems Internals & Kernel Lab",
                teacher = "Prof. Chen Gang",
                dayOfWeek = "Friday",
                startTime = "09:50",
                endTime = "12:15",
                classroom = "East Campus Bldg 3-101",
                weekRange = "Weeks 1-16",
                scheduleType = "Weekly"
            )
        )
        db.universityDao().insertTimetableClasses(timetable)

        // 2.7 Initial Study Sessions
        val initialSessions = listOf(
            StudyPlanSessionEntity(
                dayOfWeek = "Monday",
                startTime = "18:00",
                endTime = "19:15",
                title = "Computer Vision: Point Cloud Invariance",
                category = "Study",
                courseOrTopic = "CS305"
            ),
            StudyPlanSessionEntity(
                dayOfWeek = "Monday",
                startTime = "19:30",
                endTime = "20:30",
                title = "HSK 5 Academic Vocabulary Review",
                category = "Chinese Practice",
                courseOrTopic = "CH302"
            ),
            StudyPlanSessionEntity(
                dayOfWeek = "Monday",
                startTime = "20:45",
                endTime = "22:00",
                title = "LiDAR-FogNet PointNeXt Baseline Evaluation",
                category = "Research",
                courseOrTopic = "Thesis Research"
            ),
            StudyPlanSessionEntity(
                dayOfWeek = "Tuesday",
                startTime = "14:00",
                endTime = "16:00",
                title = "xv6 Memory Management: Copy-on-Write Fork",
                category = "Assignment",
                courseOrTopic = "CS308"
            ),
            StudyPlanSessionEntity(
                dayOfWeek = "Tuesday",
                startTime = "18:30",
                endTime = "20:00",
                title = "Algorithms: Max-Flow Proofs & Ford-Fulkerson",
                category = "Study",
                courseOrTopic = "CS301"
            ),
            StudyPlanSessionEntity(
                dayOfWeek = "Wednesday",
                startTime = "18:00",
                endTime = "20:00",
                title = "Computer Vision Midterm Presentation Outline",
                category = "Presentation Prep",
                courseOrTopic = "CS305"
            ),
            StudyPlanSessionEntity(
                dayOfWeek = "Thursday",
                startTime = "18:00",
                endTime = "20:30",
                title = "Exam Prep: Graph Cut Reductions & DP Bounds",
                category = "Exam Prep",
                courseOrTopic = "CS301"
            ),
            StudyPlanSessionEntity(
                dayOfWeek = "Friday",
                startTime = "14:00",
                endTime = "16:30",
                title = "Weekly Code Clean & Git Commit",
                category = "Research",
                courseOrTopic = "Research Lab"
            )
        )
        db.universityDao().insertStudySessions(initialSessions)

        // 2.8 Initial Exam Plans
        val examPlan1 = ExamPlanEntity(
            courseName = "Advanced Algorithms & Optimization",
            courseCode = "CS301",
            examDate = "Nov 04, 2026 (Week 9)",
            topics = "Max-Flow Min-Cut Theorem, Amortized Analysis (Potential method), FFT Polynomial Multiplication, NP-Hard Reductions",
            difficulty = "Hard",
            prepLevelPercent = 55,
            generatedSchedule = "Phase 1: Graph algorithms & Network Flow proofs (Oct 20-25)\nPhase 2: Amortized complexity & dynamic programming proofs (Oct 26-29)\nPhase 3: Timed past exam problem sets (Oct 30-Nov 02)\nPhase 4: Cheat-sheet formulation and rest (Nov 03)"
        )
        val examPlan2 = ExamPlanEntity(
            courseName = "Operating Systems Internals",
            courseCode = "CS308",
            examDate = "Nov 12, 2026 (Week 10)",
            topics = "xv6 Virtual Memory Paging, Trap Handling, Spinlocks, Inode File Systems",
            difficulty = "Moderate",
            prepLevelPercent = 70,
            generatedSchedule = "Phase 1: Review kernel trapframe structures and page table walks (Nov 01-04)\nPhase 2: Concurrent locking & condition variables (Nov 05-08)\nPhase 3: File system logging and crash-recovery mock questions (Nov 09-11)"
        )
        db.universityDao().insertExamPlan(examPlan1)
        db.universityDao().insertExamPlan(examPlan2)

        // 2.9 Initial Presentation Plan
        val presPlan = PresentationPlanEntity(
            courseName = "Computer Vision & Pattern Recognition (CS305)",
            presentationTitle = "Point Cloud Fog-Robustness Benchmark & Physics-Informed Inversion",
            targetDate = "Oct 28, 2026",
            stagesSummary = "Midterm technical seminar presented to Professor Zhang Lin and laboratory graduate students.",
            stage1Completed = true,
            stage2Completed = true,
            stage3Completed = false,
            stage4Completed = false,
            stage5Completed = false
        )
        db.universityDao().insertPresentationPlan(presPlan)

        // 3. Academic Tasks
        val tasks = listOf(
            AcademicTaskEntity(
                courseName = "Computer Vision & Pattern Recognition",
                title = "Implement Point Cloud Fog Invariance Module in PyTorch",
                type = "Project",
                deadline = "In 4 Days (Oct 5)",
                priority = "High",
                isCompleted = false,
                description = "Build the self-attention feature fusion layer and evaluate mIoU against baseline PointNeXt.",
                gradingWeight = "20%"
            ),
            AcademicTaskEntity(
                courseName = "Operating Systems Internals & Kernel Lab",
                title = "xv6 Memory Management: Copy-on-Write Fork Implementation",
                type = "Assignment",
                deadline = "In 8 Days (Oct 9)",
                priority = "High",
                isCompleted = false,
                description = "Modify usertrap() and copyuvm() to implement page-fault based lazy allocation on fork().",
                gradingWeight = "15%"
            ),
            AcademicTaskEntity(
                courseName = "Advanced Technical & Academic Chinese",
                title = "Draft 500-character Chinese Abstract for Research Proposal",
                type = "Assignment",
                deadline = "In 12 Days (Oct 13)",
                priority = "Medium",
                isCompleted = false,
                description = "Summarize the research motivation, methodology, and expected results using formal academic Chinese terminology.",
                gradingWeight = "10%"
            ),
            AcademicTaskEntity(
                courseName = "Advanced Algorithms & Optimization",
                title = "Problem Set 4: Max-Flow Min-Cut & Ford-Fulkerson Reductions",
                type = "Assignment",
                deadline = "In 16 Days (Oct 17)",
                priority = "Medium",
                isCompleted = false,
                description = "Complete proofs on bipartite graph matching and image segmentation graph-cut energy minimization.",
                gradingWeight = "10%"
            )
        )
        for (task in tasks) {
            db.universityDao().insertTask(task)
        }

        // 4. Research Project
        val research = ResearchProjectEntity(
            title = "Robust Semi-Supervised 3D LiDAR Semantic Segmentation in Maritime Coastal Fog",
            researchArea = "Computer Vision & 3D LiDAR Perception",
            description = "Empirical investigation of physics-informed scattering inversion and deformable attention for autonomous port vehicle perception under coastal marine fog.",
            researchProblem = "Severe point cloud attenuation and pseudo-cluster noise caused by atmospheric Mie backscattering in northern Chinese coastal ports (Qinhuangdao).",
            researchQuestion = "How can feature-level consistency regularization and physics-informed scattering priors prevent catastrophic representation degradation of 3D point clouds in adverse maritime weather?",
            objectives = "1. Construct real-world coastal fog benchmark\n2. Design differentiable scattering compensation module\n3. Outperform PointNeXt and Cylinder3D by >6% mIoU",
            researchGap = "Existing SOTA LiDAR segmentation models (PointNeXt, SphereFormer, MinkUNet) assume clean atmospheric transmission. Under real coastal fog in northern Chinese ports (such as Qinhuangdao), backscatter creates spurious cluster noise, dropping mIoU by over 32.4%.",
            hypotheses = "1. Atmospheric attenuation of LiDAR beam intensity follows an exponential decay model that can be inverted via differentiable depth-dependent priors.\n2. Cross-view spatio-temporal temporal consistency between consecutive sweeps enables pseudo-label filtering of transient fog particles without requiring dense manual fog annotations.",
            datasets = "SemanticKITTI + Qinhuangdao Maritime Port Adverse Weather LiDAR Dataset (12,000 real coastal scans collected with Hesai Pandar64)",
            methodology = "Dual-branch consistency architecture with depth-aware scattering compensation and spatial density gating.",
            modelsAlgorithms = "PointFog-SAM: Sparse Voxel Transformer + SAM Deformable Cross-Attention + Physics Inversion Loss",
            experiments = "1. Baseline evaluation across clean vs simulated fog vs real port fog\n2. Dual-branch consistency architecture with depth-aware scattering compensation\n3. Ablation of consistency thresholding and spatial density gating",
            baselines = "PointNeXt (NeurIPS 2022), SphereFormer (CVPR 2023), Cylinder3D (CVPR 2021), SalsaNext",
            metrics = "mIoU (mean Intersection over Union), Accuracy, Precision/Recall on vulnerable classes (pedestrians, small buoys, crane cables), Runtime FPS (NVIDIA Jetson AGX Orin)",
            results = "Current prototype achieves 64.8% mIoU on synthetic fog (outperforming PointNeXt 56.2% by +8.6% mIoU), 58.4% on real Qinhuangdao port fog.",
            codeRepo = "https://github.com/alexei-ysu-cs/FogLiDAR-Seg",
            papersSummary = "Surveyed 28 key papers including Weather4D, Defog-LiDAR, and PointNeXt. Synthesized literature gap into 3 core architectural challenges.",
            currentStatus = "Implementation",
            manuscriptStatus = "Drafting",
            targetVenue = "CVPR 2027",
            targetPublication = "CVPR 2027 (Primary target: Main Conference; Backup: IEEE RA-L / IROS)",
            notes = "Supervised by Prof. Zhang Lin at AI Center. Weekly lab group meeting on Thursday afternoons.",
            isActive = true,
            progressPercent = 65
        )
        val projId = db.researchDao().insertProject(research)

        // 4.1 Research Milestones
        val milestones = listOf(
            ResearchMilestoneEntity(projectId = projId, title = "Literature review & problem formulation", deadline = "Completed (Aug 15)", status = "Completed", isCompleted = true, orderIndex = 1, notes = "Analyzed 28 papers on adverse weather point cloud perception."),
            ResearchMilestoneEntity(projectId = projId, title = "Qinhuangdao Port LiDAR dataset collection & calibration", deadline = "Completed (Sep 10)", status = "Completed", isCompleted = true, orderIndex = 2, notes = "Recorded 12,000 scans with Hesai Pandar64 in heavy coastal fog."),
            ResearchMilestoneEntity(projectId = projId, title = "Implement PointNeXt & Cylinder3D baselines", deadline = "Completed (Sep 28)", status = "Completed", isCompleted = true, orderIndex = 3, notes = "Established 56.2% mIoU baseline under simulated fog."),
            ResearchMilestoneEntity(projectId = projId, title = "Implement Koschmieder physics scattering inversion layer", deadline = "Oct 15, 2026", status = "In Progress", isCompleted = false, orderIndex = 4, notes = "Formulate differentiable attenuation loss in PyTorch."),
            ResearchMilestoneEntity(projectId = projId, title = "Ablation study on density gating & temporal consistency", deadline = "Nov 01, 2026", status = "Pending", isCompleted = false, orderIndex = 5, notes = "Evaluate ablation contributions across synthetic vs real foggy port test splits."),
            ResearchMilestoneEntity(projectId = projId, title = "First complete manuscript draft (Overleaf LaTeX)", deadline = "Nov 15, 2026", status = "Pending", isCompleted = false, orderIndex = 6, notes = "All 8 pages formatted in standard CVPR IEEE style."),
            ResearchMilestoneEntity(projectId = projId, title = "Internal lab review with Prof. Zhang Lin", deadline = "Nov 20, 2026", status = "Pending", isCompleted = false, orderIndex = 7, notes = "Address feedback from senior lab PhD candidates."),
            ResearchMilestoneEntity(projectId = projId, title = "Final camera-ready CVPR 2027 submission", deadline = "Nov 25, 2026", status = "Pending", isCompleted = false, orderIndex = 8, notes = "Submit PDF manuscript and supplementary code/video.")
        )
        db.researchDao().insertMilestones(milestones)

        // 4.2 Research Papers & Literature Matrix
        val papers = listOf(
            ResearchPaperEntity(
                projectId = projId,
                title = "PointNeXt: Revisiting PointNet++ with Improved Training and Scaling Strategies",
                authors = "Guocheng Qian, Yuchen Li, Houwen Peng, Jinjie Mai, Hasan Hammoud, Mohamed Elhoseiny, Bernard Ghanem",
                year = "2022",
                venue = "NeurIPS",
                doi = "10.48550/arXiv.2206.04670",
                url = "https://arxiv.org/abs/2206.04670",
                abstractText = "PointNet++ has been a baseline for 3D point cloud understanding. We modernize PointNeXt with inverted residual MLP blocks, channel scaling, and receptive field optimization.",
                researchProblem = "PointNet++ suffers from slow training and high latency, limiting scalability for dense outdoor autonomous driving sweeps.",
                method = "Inverted residual MLP blocks with separable convolutional grouping and enhanced data augmentations.",
                dataset = "ScanObjectNN, S3DIS, SemanticKITTI",
                evaluationMetrics = "mIoU, Overall Accuracy (OA), Latency (ms)",
                mainFindings = "Achieved 70.3% mIoU on SemanticKITTI with 10x faster training time than original PointNet++.",
                limitations = "Assumes clear atmospheric transmission. When evaluated under dense fog, mIoU degrades by over 30%.",
                relevanceToProject = "Direct baseline for our point cloud backbone architecture.",
                researchGapContribution = "Lacks atmospheric attenuation modeling for adverse weather.",
                personalNotes = "Used as primary backbone for PointFog-SAM feature extraction.",
                sourceContentSnippet = "[SOURCE CONTENT]: \"PointNeXt achieves state-of-the-art efficiency and accuracy on SemanticKITTI benchmark without voxelization overhead.\"",
                aiInterpretationSnippet = "[AI INTERPRETATION]: Excellent baseline model; requires custom pre-filtering layer to handle maritime fog particles."
            ),
            ResearchPaperEntity(
                projectId = projId,
                title = "Defog-LiDAR: Seeing Through Coastal Marine Fog with Polarized LiDAR",
                authors = "Zhang Wei, Chen Ming, Liu Yang, Prof. Lin Hai",
                year = "2024",
                venue = "IEEE TPAMI",
                doi = "10.1109/TPAMI.2024.3382910",
                url = "https://ieeexplore.ieee.org/document/defog-lidar",
                abstractText = "We present Defog-LiDAR, analyzing multi-polarization backscatter signals to remove droplet reflections in maritime navigation.",
                researchProblem = "Backscatter reflections from suspended water droplets obscure target objects in maritime harbors.",
                method = "Polarization-difference matrix filtering coupled with physical depth-dependent scattering model.",
                dataset = "Coastal Harbor LiDAR Dataset (Pandar64 + Custom Polarizer)",
                evaluationMetrics = "Point Cloud SNR, Reconstruction Error (RMSE), mIoU",
                mainFindings = "Improves SNR by 14.2 dB in moderate to dense coastal fog.",
                limitations = "Requires specialized hardware polarizers not available on standard commercial autonomous trucks.",
                relevanceToProject = "Provides the optical physics theory for software-only scattering inversion.",
                researchGapContribution = "How to achieve similar defogging using purely software-based neural scattering priors on unpolarized standard LiDAR.",
                personalNotes = "Key reference for our physics-guided loss function equations.",
                sourceContentSnippet = "[SOURCE CONTENT]: \"Backscattering follows Koschmieder exponential extinction where intensity diminishes with distance d according to e^(-beta * d).\"",
                aiInterpretationSnippet = "[AI INTERPRETATION]: Strong mathematical formulation; can be adapted as a differentiable loss without requiring polarization hardware."
            ),
            ResearchPaperEntity(
                projectId = projId,
                title = "Cylinder3D: An Effective 3D Framework for Driving-Scene LiDAR Semantic Segmentation",
                authors = "Xinge Zhu, Hui Zhou, Tai Wang, Fangzhou Shen, Xun Dong, Dahua Lin",
                year = "2021",
                venue = "CVPR",
                doi = "10.1109/CVPR46437.2021.00690",
                url = "https://arxiv.org/abs/2011.10033",
                abstractText = "Outdoor LiDAR sweeps have non-uniform density. We propose cylindrical voxelization and asymmetric 3D convolution networks.",
                researchProblem = "Standard Cartesian voxel grids waste memory on sparse distant points and over-quantize nearby dense objects.",
                method = "Cylindrical coordinate voxel partitioning and asymmetric residual convolutional blocks.",
                dataset = "SemanticKITTI, nuScenes",
                evaluationMetrics = "mIoU, Precision, Recall",
                mainFindings = "Won 1st place in SemanticKITTI Challenge with 68.9% mIoU.",
                limitations = "Heavy memory consumption on 3D convolutions; fails on spurious floating noise clusters in foggy atmospheres.",
                relevanceToProject = "Second competitive baseline to test in our comparative evaluation.",
                researchGapContribution = "Cylindrical voxels near the sensor origin suffer extreme backscatter cluster distortion in foggy port zones.",
                personalNotes = "Benchmarked in EXP-003.",
                sourceContentSnippet = "[SOURCE CONTENT]: \"Cylindrical partitioning achieves consistent point density per voxel across radial distance r and azimuth theta.\"",
                aiInterpretationSnippet = "[AI INTERPRETATION]: Outstanding architectural design; however, dense fog near the sensor head creates thousands of false positive voxels."
            )
        )
        db.researchDao().insertPapers(papers)

        // 4.3 Research Experiments
        val experiments = listOf(
            ResearchExperimentEntity(
                projectId = projId,
                experimentId = "EXP-001",
                name = "PointNeXt Baseline on Simulated Port Fog",
                date = "2026-09-22",
                dataset = "SemanticKITTI (Clean vs Synthetic Fog Beta=0.05)",
                features = "3D Coordinates (X, Y, Z) + Intensity (I)",
                model = "PointNeXt-S (Hierarchical MLP)",
                baseline = "PointNet++ (CVPR 2017)",
                hyperparameters = "lr=0.001, batch_size=16, weight_decay=1e-4, epochs=100",
                randomSeed = "42",
                trainValTestStrategy = "70% Train, 15% Val, 15% Test split by sequence ID",
                evaluationMetrics = "mIoU, Accuracy, Latency (ms)",
                results = "Clean: 70.2% mIoU | Foggy: 56.2% mIoU (-14.0% drop)",
                metricF1 = 0.582f,
                metricMcc = 0.514f,
                metricPrecision = 0.610f,
                metricRecall = 0.558f,
                metricAuc = 0.812f,
                customMetricName = "mIoU Foggy",
                customMetricValue = "56.2%",
                notes = "Confirmed catastrophic representation collapse under synthetic fog.",
                codeVersionRef = "git commit 7f8a91b (branch: baseline-pointnext)"
            ),
            ResearchExperimentEntity(
                projectId = projId,
                experimentId = "EXP-002",
                name = "PointFog-SAM with Koschmieder Scattering Inversion",
                date = "2026-09-29",
                dataset = "SemanticKITTI + Qinhuangdao Port LiDAR Dataset",
                features = "3D Coordinates + Normalized Intensity + Range (R)",
                model = "PointFog-SAM (Ours: PointNeXt + Inversion Layer + SAM Priors)",
                baseline = "EXP-001 PointNeXt",
                hyperparameters = "lr=0.0005, batch_size=8, lambda_phys=0.25, seed=42",
                randomSeed = "42",
                trainValTestStrategy = "Sequence-isolated cross-validation with identical test split",
                evaluationMetrics = "mIoU, Accuracy, Precision, Recall",
                results = "Clean: 71.4% mIoU | Synthetic Fog: 64.8% mIoU (+8.6%) | Real Port Fog: 58.4% mIoU",
                metricF1 = 0.665f,
                metricMcc = 0.602f,
                metricPrecision = 0.684f,
                metricRecall = 0.648f,
                metricAuc = 0.879f,
                customMetricName = "mIoU Foggy",
                customMetricValue = "64.8%",
                notes = "Physics inversion loss successfully filtered 82% of spurious particle clusters.",
                codeVersionRef = "git commit 3b1e84c (branch: feat/scattering-inversion)"
            ),
            ResearchExperimentEntity(
                projectId = projId,
                experimentId = "EXP-003",
                name = "Cylinder3D Comparative Baseline Evaluation",
                date = "2026-10-01",
                dataset = "Qinhuangdao Port LiDAR Dataset (Real Coastal Fog)",
                features = "Cylindrical Coordinates (rho, phi, z) + Intensity",
                model = "Cylinder3D (Asymmetric 3D CNN)",
                baseline = "PointNeXt-S",
                hyperparameters = "lr=0.002, batch_size=4, optimizer=AdamW, epochs=80",
                randomSeed = "42",
                trainValTestStrategy = "Identical harbor validation sweep split",
                evaluationMetrics = "mIoU, Memory (GB), Inference FPS",
                results = "Real Port Fog: 57.1% mIoU | Inference: 14.2 FPS (Jetson Orin)",
                metricF1 = 0.590f,
                metricMcc = 0.528f,
                metricPrecision = 0.622f,
                metricRecall = 0.562f,
                metricAuc = 0.825f,
                customMetricName = "mIoU Real Fog",
                customMetricValue = "57.1%",
                notes = "Higher GPU VRAM consumption (9.8 GB) than PointFog-SAM (4.2 GB).",
                codeVersionRef = "git commit a94f28e (branch: eval/cylinder3d)"
            )
        )
        db.researchDao().insertExperiments(experiments)

        // 4.4 Manuscript Sections
        val manuscriptSections = listOf(
            ManuscriptSectionEntity(projectId = projId, sectionName = "Title", status = "Completed", wordCount = 14, targetWordCount = 20, notes = "Selected: Robust Semi-Supervised 3D LiDAR Semantic Segmentation in Maritime Coastal Fog."),
            ManuscriptSectionEntity(projectId = projId, sectionName = "Abstract", status = "Drafting", wordCount = 210, targetWordCount = 250, notes = "Drafted 210 words. Summarizes problem, physics prior, and 8.6% mIoU improvement."),
            ManuscriptSectionEntity(projectId = projId, sectionName = "Introduction", status = "Drafting", wordCount = 850, targetWordCount = 1000, notes = "Motivates autonomous container truck safety in northern Chinese coastal ports."),
            ManuscriptSectionEntity(projectId = projId, sectionName = "Related Work", status = "Review", wordCount = 920, targetWordCount = 950, notes = "Covered 3D point cloud segmentation, adverse weather restoration, and foundation models."),
            ManuscriptSectionEntity(projectId = projId, sectionName = "Methodology", status = "Drafting", wordCount = 1200, targetWordCount = 1400, notes = "Formulates Koschmieder scattering inversion and deformable cross-attention equations."),
            ManuscriptSectionEntity(projectId = projId, sectionName = "Experimental Setup", status = "Completed", wordCount = 650, targetWordCount = 700, notes = "Detailed Hesai Pandar64 sensor calibration and train/test validation splits."),
            ManuscriptSectionEntity(projectId = projId, sectionName = "Results", status = "Drafting", wordCount = 780, targetWordCount = 900, notes = "Included comparative tables across PointNeXt, Cylinder3D, and PointFog-SAM."),
            ManuscriptSectionEntity(projectId = projId, sectionName = "Discussion", status = "Not Started", wordCount = 0, targetWordCount = 600, notes = "Will analyze failure cases on thin crane cables under extreme 95% humidity."),
            ManuscriptSectionEntity(projectId = projId, sectionName = "Limitations", status = "Drafting", wordCount = 250, targetWordCount = 300, notes = "Honest documentation: evaluated primarily in coastal fog, not heavy snow."),
            ManuscriptSectionEntity(projectId = projId, sectionName = "Conclusion", status = "Not Started", wordCount = 0, targetWordCount = 350, notes = "To be drafted after internal review."),
            ManuscriptSectionEntity(projectId = projId, sectionName = "References", status = "Review", wordCount = 420, targetWordCount = 500, notes = "38 verified references formatted in IEEE CVPR .bib database.")
        )
        db.researchDao().insertManuscriptSections(manuscriptSections)

        // 4.5 Reproducibility Checklist
        val reproItems = listOf(
            ReproducibilityItemEntity(projectId = projId, itemKey = "data_id", title = "Dataset identified", isCompleted = true, details = "SemanticKITTI + Qinhuangdao Port Adverse Weather LiDAR Dataset", category = "Data"),
            ReproducibilityItemEntity(projectId = projId, itemKey = "data_ver", title = "Dataset version recorded", isCompleted = true, details = "Version 1.2 with 12,000 calibrated point cloud frames", category = "Data"),
            ReproducibilityItemEntity(projectId = projId, itemKey = "data_split", title = "Data split documented", isCompleted = true, details = "70% Train, 15% Val, 15% Test split by continuous sequence timestamp", category = "Data"),
            ReproducibilityItemEntity(projectId = projId, itemKey = "seed", title = "Random seed recorded", isCompleted = true, details = "torch.manual_seed(42), np.random.seed(42), cudnn.deterministic=True", category = "Parameters"),
            ReproducibilityItemEntity(projectId = projId, itemKey = "features", title = "Features documented", isCompleted = true, details = "X, Y, Z coordinates + calibrated intensity + spherical range rho", category = "Parameters"),
            ReproducibilityItemEntity(projectId = projId, itemKey = "baselines", title = "Baselines documented", isCompleted = true, details = "PointNeXt (NeurIPS 2022) and Cylinder3D (CVPR 2021)", category = "Method"),
            ReproducibilityItemEntity(projectId = projId, itemKey = "hyperparams", title = "Hyperparameters documented", isCompleted = true, details = "lr=5e-4, batch=8, CosineAnnealingLR, weight_decay=1e-4", category = "Parameters"),
            ReproducibilityItemEntity(projectId = projId, itemKey = "metrics", title = "Metrics documented", isCompleted = true, details = "mIoU across all 19 SemanticKITTI classes, Precision, Recall", category = "Method"),
            ReproducibilityItemEntity(projectId = projId, itemKey = "environment", title = "Environment recorded", isCompleted = true, details = "Ubuntu 22.04 LTS, Python 3.10.12, CUDA 12.1", category = "Compute"),
            ReproducibilityItemEntity(projectId = projId, itemKey = "dependencies", title = "Dependencies recorded", isCompleted = true, details = "PyTorch 2.3.0, Open3D 0.18.0, PyTorch Geometric, MinkowskiEngine", category = "Compute"),
            ReproducibilityItemEntity(projectId = projId, itemKey = "hardware", title = "Hardware recorded", isCompleted = true, details = "NVIDIA RTX 4090 24GB + Intel Core i9-13900K, 64GB DDR5 RAM", category = "Compute"),
            ReproducibilityItemEntity(projectId = projId, itemKey = "code_ver", title = "Code/version recorded", isCompleted = true, details = "Git commit hash tagged as release-v0.6-cvpr-ready", category = "Code"),
            ReproducibilityItemEntity(projectId = projId, itemKey = "results_saved", title = "Results saved", isCompleted = true, details = "TensorBoard event files and validation confusion matrices logged to WandB", category = "Results"),
            ReproducibilityItemEntity(projectId = projId, itemKey = "figures_gen", title = "Figures generated", isCompleted = true, details = "Vector PDF qualitative visualizations rendered in Open3D with colorbar", category = "Visuals"),
            ReproducibilityItemEntity(projectId = projId, itemKey = "tables_gen", title = "Tables generated", isCompleted = true, details = "LaTeX tabular code generated automatically from pandas script", category = "Visuals")
        )
        db.researchDao().insertReproducibilityItems(reproItems)

        // 5. Skills Items
        val skills = listOf(
            SkillItemEntity(category = "Programming", name = "Python (PyTorch, NumPy, Cython)", level = "Advanced", isCompleted = true, description = "Vectorized computation, PyTorch custom autograd functions, multi-GPU DDP training."),
            SkillItemEntity(category = "Programming", name = "C/C++ (Modern C++17/20, STL, CMake)", level = "Intermediate", isCompleted = true, description = "Memory management, pointers, smart pointers, OpenCV C++ API, CUDA kernel basics."),
            SkillItemEntity(category = "Programming", name = "Java & Object Oriented Design", level = "Intermediate", isCompleted = true, description = "Design patterns, JVM memory model, concurrency, Spring Boot & Android Compose basics."),
            SkillItemEntity(category = "Programming", name = "CUDA & GPU Parallel Architecture", level = "Beginner", isCompleted = false, description = "Warp synchronization, shared memory tiling, thread blocks, roofline model optimization."),

            SkillItemEntity(category = "Core CS", name = "Data Structures & Algorithms", level = "Advanced", isCompleted = true, description = "Trees, graphs, heaps, dynamic programming, network flow, amortized complexity."),
            SkillItemEntity(category = "Core CS", name = "Operating Systems & Concurrency", level = "Intermediate", isCompleted = true, description = "Kernel design, paging, locks, semaphores, xv6 Unix system calls, context switching."),
            SkillItemEntity(category = "Core CS", name = "Computer Networks", level = "Intermediate", isCompleted = true, description = "TCP/IP socket programming, congestion control, HTTP/3, QUIC, network packet analysis."),
            SkillItemEntity(category = "Core CS", name = "Databases & SQL Optimization", level = "Intermediate", isCompleted = true, description = "B-tree indexing, ACID transactions, relational normalization, NoSQL key-value stores."),
            SkillItemEntity(category = "Core CS", name = "Distributed Systems & System Design", level = "Beginner", isCompleted = false, description = "Consensus (Raft/Paxos), sharding, CAP theorem, message queues (Kafka), microservices."),

            SkillItemEntity(category = "AI", name = "Deep Learning Fundamentals", level = "Advanced", isCompleted = true, description = "Backprop, gradient descent variants, regularization, batchnorm/layernorm, attention mechanisms."),
            SkillItemEntity(category = "AI", name = "Vision Transformers (ViT & Swin)", level = "Advanced", isCompleted = true, description = "Patch projection, multi-head self-attention, shifted windows, positional encodings."),
            SkillItemEntity(category = "AI", name = "Diffusion & Generative Models", level = "Intermediate", isCompleted = false, description = "DDPM, DDIM, Score-based models, Latent Diffusion, Stable Diffusion architecture."),
            SkillItemEntity(category = "AI", name = "LLMs & Agentic Systems", level = "Intermediate", isCompleted = true, description = "Prompt engineering, RAG, function calling, tool use, parameter-efficient fine-tuning (LoRA)."),
            SkillItemEntity(category = "AI", name = "Reinforcement Learning", level = "Beginner", isCompleted = false, description = "MDPs, Q-learning, Policy Gradients, PPO, actor-critic frameworks."),

            SkillItemEntity(category = "Computer Vision", name = "3D Point Cloud Processing", level = "Research-Ready", isCompleted = true, description = "PointNet, PointNeXt, Sparse 3D Convolution, MinkowskiEngine, LiDAR sensor modeling."),
            SkillItemEntity(category = "Computer Vision", name = "Object Detection & Segmentation", level = "Advanced", isCompleted = true, description = "YOLO series, Mask R-CNN, DETR, Segment Anything (SAM), panoptic segmentation."),
            SkillItemEntity(category = "Computer Vision", name = "Adverse Weather Vision Restoration", level = "Advanced", isCompleted = true, description = "Dehazing (DCP, GridDehazeNet), rain removal, physics-based scattering inversion."),
            SkillItemEntity(category = "Computer Vision", name = "Multimodal Vision-Language Models", level = "Intermediate", isCompleted = false, description = "CLIP, BLIP-2, LLaVA, spatial visual grounding, cross-attention alignment."),

            SkillItemEntity(category = "Research Skills", name = "LaTeX & Overleaf Manuscript Authoring", level = "Advanced", isCompleted = true, description = "CVPR/IEEE IEEEtran templates, TikZ diagramming, BibTeX reference management."),
            SkillItemEntity(category = "Research Skills", name = "Scientific Writing & Research Rigor", level = "Intermediate", isCompleted = true, description = "Paper structuring, abstract writing, rebuttal craft, statistical significance testing."),
            SkillItemEntity(category = "Research Skills", name = "Reproducibility & Ablation Design", level = "Intermediate", isCompleted = true, description = "Fair benchmarking, seed averaging, ablation matrix design, parameter counting.")
        )
        db.skillDao().insertAll(skills)

        // 6. Chinese Learning Phrases (Technical, Academic, Life in China)
        val phrases = listOf(
            ChinesePhraseEntity(
                category = "Technical CS",
                hanzi = "卷积神经网络",
                pinyin = "juǎn jī shén jīng wǎng luò",
                english = "Convolutional Neural Network (CNN)",
                exampleZh = "这篇论文提出了一种新型的轻量化卷积神经网络架构。",
                exampleEn = "This paper proposes a novel lightweight convolutional neural network architecture.",
                notes = "Frequently used in lab meetings and literature discussions."
            ),
            ChinesePhraseEntity(
                category = "Technical CS",
                hanzi = "点云语义分割",
                pinyin = "diǎn yún yǔ yì fēn gē",
                english = "Point Cloud Semantic Segmentation",
                exampleZh = "我们的实验评估了模型在复杂海港点云语义分割上的表现。",
                exampleEn = "Our experiments evaluated the model's performance on complex seaport point cloud semantic segmentation.",
                notes = "Core phrase for your research presentation at Yanshan University."
            ),
            ChinesePhraseEntity(
                category = "Technical CS",
                hanzi = "显存溢出",
                pinyin = "xiǎn cún yì chū",
                english = "CUDA Out of Memory (OOM)",
                exampleZh = "Batch size 设置过大会导致显存溢出，建议开启混合精度训练。",
                exampleEn = "Setting the batch size too large causes CUDA out of memory; enabling mixed precision training is recommended.",
                notes = "Crucial lab engineering jargon."
            ),
            ChinesePhraseEntity(
                category = "Academic",
                hanzi = "导师 / 指导教师",
                pinyin = "dǎo shī / zhǐ dǎo jiào shī",
                english = "Academic Advisor / Supervisor",
                exampleZh = "我已经向张老师预约了周五下午讨论论文开题报告。",
                exampleEn = "I have booked an appointment with Prof. Zhang for Friday afternoon to discuss the thesis proposal.",
                notes = "Always address professors respectfully in emails as [Surname] + 老师 or 教授."
            ),
            ChinesePhraseEntity(
                category = "Academic",
                hanzi = "开题报告与答辩",
                pinyin = "kāi tí bào gào yǔ dá biàn",
                english = "Thesis Proposal and Oral Defense",
                exampleZh = "下学期末将进行本科毕业设计的开题答辩。",
                exampleEn = "The undergraduate graduation design proposal defense will take place at the end of next semester.",
                notes = "Standard milestone requirement for Chinese university degrees."
            ),
            ChinesePhraseEntity(
                category = "Campus & Yanshan Life",
                hanzi = "留学生办公室",
                pinyin = "liú xué shēng bàn gōng shì",
                english = "International Students Office (ISO)",
                exampleZh = "签证和居留许可续签需要先到留学生办公室开具在读证明。",
                exampleEn = "Visa and residence permit renewals require an enrollment certificate from the International Students Office first.",
                notes = "Located in the International Exchange College building at Yanshan University."
            ),
            ChinesePhraseEntity(
                category = "Campus & Yanshan Life",
                hanzi = "境外人员临时住宿登记表",
                pinyin = "jìng wài rén yuán lín shí zhù sù dēng jì biǎo",
                english = "Registration Form of Temporary Residence for Visitors",
                exampleZh = "搬入校外公寓后必须在24小时内到辖区派出所办理住宿登记。",
                exampleEn = "After moving into an off-campus apartment, you must register at the local police station within 24 hours.",
                notes = "CRITICAL LEGAL COMPLIANCE: Mandatory under Chinese immigration regulations."
            ),
            ChinesePhraseEntity(
                category = "Campus & Yanshan Life",
                hanzi = "校园一卡通充值",
                pinyin = "xiào yuán yī kǎ tōng chōng zhí",
                english = "Campus Smart Card Top-up",
                exampleZh = "你可以用完美校园或者微信绑定一卡通进行饭卡充值和洗澡水费缴纳。",
                exampleEn = "You can bind your campus card with WeChat to top up meal balances and shower water fees.",
                notes = "Used at YSU canteens, libraries, and dormitories."
            )
        )
        db.chineseDao().insertAll(phrases)

        // 7. Knowledge Base items
        val knowledge = listOf(
            KnowledgeItemEntity(
                title = "China Foreign Graduate Employment & Work Permit Policies (2025/2026 Verification)",
                category = "Visa/Immigration",
                content = "Under official National Immigration Administration & Ministry of Human Resources policies:\n1. Master's/PhD graduates from recognized Chinese universities (like Double First-Class / Key Universities) are eligible to apply directly for Foreigner's Work Permit (Category B or A) WITHOUT requiring 2 years of prior overseas work experience.\n2. Bachelor's graduates: Eligible if GPA is top tier, or employed in designated Free Trade Zones / High-Tech parks, or scoring 60+ points under the Classification Evaluation standard.\n3. Student Internships: International students MUST obtain university approval and an internship endorsement annotation (加注) on their Residence Permit from the Exit-Entry Administration before starting any off-campus internship. Unannotated work is legally classified as illegal employment (非法就业).",
                tags = "Work Permit, Immigration, Employment, Category B, YSU, Internship",
                sourceRef = "National Immigration Administration of China & Ministry of Human Resources Notice No. 34",
                isVerified = true
            ),
            KnowledgeItemEntity(
                title = "Yanshan University International Student Survival Guide: Qinhuangdao & Campus",
                category = "Campus Guide",
                content = "Campus: Yanshan University (YSU) is situated in Haigang District, Qinhuangdao, Hebei Province, close to the Bohai Sea coast and Olympic Sports Center.\n- Climate: Temperate coastal monsoon. Cold windy winters (down to -8°C), pleasant summers.\n- Transport: Qinhuangdao Railway Station connects to Beijing in ~1.5 hours via high-speed train (G/D trains). Bus 34/6 links East and West campuses.\n- Food/Halal: Muslim canteens (清真食堂) available at both East Campus (First Canteen 2nd floor) and West Campus.\n- Medical: First Hospital of Qinhuangdao (秦皇岛市第一医院) is the primary certified municipal hospital for student medical insurance reimbursements.",
                tags = "Yanshan University, Qinhuangdao, Campus Life, Climate, High-Speed Rail, Hospital",
                sourceRef = "YSU International Education College Handbook",
                isVerified = true
            ),
            KnowledgeItemEntity(
                title = "Top Computer Vision & AI Publication Deadlines Roadmap (2026/2027 Cycle)",
                category = "Paper Summary",
                content = "Target venues for point cloud and multimodal perception:\n- CVPR 2027: Paper abstract deadline mid-November, full paper late November. Rebuttal in January.\n- ICCV / ECCV (alternating years): Submission deadline typically early March.\n- NeurIPS: Abstract mid-May, full submission late May. Double-blind 9-page limit.\n- Review Criteria: Core problem significance, technical novelty over strong baselines (PointNeXt, MinkUNet), ablation completeness, reproducibility code availability.",
                tags = "CVPR, NeurIPS, ICCV, Deadlines, Computer Vision, Paper Submission",
                sourceRef = "IEEE Computer Society & NeurIPS Foundation Official CFP",
                isVerified = true
            )
        )
        for (item in knowledge) {
            db.knowledgeDao().insertItem(item)
        }

        // 8. Roadmap Goals
        val goals = listOf(
            RoadmapGoalEntity(timeframe = "Daily", title = "Write 100 lines of PyTorch LiDAR data loader & consistency loss", targetDate = "Today", isCompleted = false, priority = "High"),
            RoadmapGoalEntity(timeframe = "Daily", title = "Review 25 HSK 5 technical vocabulary flashcards", targetDate = "Today", isCompleted = true, priority = "Normal"),
            RoadmapGoalEntity(timeframe = "Weekly", title = "Complete xv6 Copy-on-Write lab and pass all kernel test suites", targetDate = "This Sunday", isCompleted = false, priority = "High"),
            RoadmapGoalEntity(timeframe = "Weekly", title = "Meet Prof. Zhang Lin for FogLiDAR ablation experimental review", targetDate = "Thursday", isCompleted = false, priority = "High"),
            RoadmapGoalEntity(timeframe = "Semester", title = "Finalize CVPR 2027 paper draft with complete tables & ablation matrix", targetDate = "Nov 2026", isCompleted = false, priority = "High"),
            RoadmapGoalEntity(timeframe = "Semester", title = "Pass HSK 5 official examination with score >= 220", targetDate = "Dec 2026", isCompleted = false, priority = "High"),
            RoadmapGoalEntity(timeframe = "Long-Term", title = "Graduate Bachelor of Engineering from YSU with First-Class Honors", targetDate = "June 2027", isCompleted = false, priority = "High"),
            RoadmapGoalEntity(timeframe = "Long-Term", title = "Secure PhD admission or AI Research Scientist position at top institution", targetDate = "Fall 2027", isCompleted = false, priority = "High")
        )
        db.roadmapDao().insertAllGoals(goals)

        // 9. Personal Document Library
        val initialDocs = listOf(
            PersonalDocumentEntity(
                userEmail = "alexei.chen@ysu.edu.cn",
                fileName = "CS305_Computer_Vision_Syllabus.md",
                fileType = "MD",
                category = "ACADEMIC",
                uploadDate = "2026-09-08",
                tags = "syllabus, deadlines, grading, computer vision, ysu",
                userNotes = "Prof. Zhang Lin's core course syllabus. Midterm report 20%, Final defense 40%.",
                courseOrProject = "CS305: Computer Vision",
                content = "Course: CS305 Computer Vision & Pattern Recognition\nInstructor: Prof. Zhang Lin (School of Information Science, YSU)\nClassroom: East Campus 5-201\nSchedule: Tue/Thu 10:15 - 11:50\nRequirements:\n1. Complete 4 hands-on PyTorch programming labs.\n2. Submit Midterm Literature Report by October 18.\n3. Final graduation project presentation & defense on December 18.\nGrading: Labs (40%), Midterm (20%), Final Project Defense (40%).\nAcademic Integrity: Plagiarism strictly prohibited; code commits must be authenticated."
            ),
            PersonalDocumentEntity(
                userEmail = "alexei.chen@ysu.edu.cn",
                fileName = "PointFog_SAM_Manuscript_Draft.md",
                fileType = "MD",
                category = "RESEARCH",
                uploadDate = "2026-09-24",
                tags = "cvpr2027, 3d lidar, point cloud, fog, ablation",
                userNotes = "Primary conference draft targeting CVPR 2027 main track.",
                courseOrProject = "PointFog-SAM Research",
                content = "Title: Robust Semi-Supervised 3D LiDAR Semantic Segmentation in Maritime Coastal Fog\nAuthors: Alexei Chen-Kovalenko, Prof. Zhang Lin\nAbstract: Autonomous mobile perception in coastal industrial ports suffers catastrophic point cloud attenuation due to dense aerosol fog. We introduce PointFog-SAM, a dual-stream architecture combining 3D sparse voxel backbones with 2D foundation representations.\nMethodology: Physics-based Koschmieder scattering inversion layer with deformable cross-attention fusion.\nDatasets: Qinhuangdao Port LiDAR Maritime Dataset (12,000 scans) + SemanticKITTI.\nEvaluation: Outperforms PointNeXt baseline by +8.4% mIoU under dense maritime fog while operating at 38 FPS on NVIDIA Jetson AGX Orin.\nLimitations: Real-time inference on lower-tier 4-watt microcontrollers requires 4-bit quantization."
            ),
            PersonalDocumentEntity(
                userEmail = "alexei.chen@ysu.edu.cn",
                fileName = "xv6_Memory_COW_Lab_Report.txt",
                fileType = "TXT",
                category = "ACADEMIC",
                uploadDate = "2026-09-29",
                tags = "operating systems, kernel, copy-on-write, page faults",
                userNotes = "Lab 3 submission for Prof. Chen Gang.",
                courseOrProject = "CS308: Operating Systems",
                content = "Assignment: xv6 Copy-on-Write Fork Implementation\nStudent: Alexei Chen-Kovalenko (ID: 2024CS0892)\nKey Implementation Details:\n- Modified usertrap() to catch T_PGFLT (page fault exception 15).\n- Added physical page reference counter array indexed by pa >> 12.\n- In fork(), mapped parent pages read-only with PTE_W cleared and PTE_COW bit set.\n- Results: Passed all xv6 cowtest suites with zero memory leaks."
            ),
            PersonalDocumentEntity(
                userEmail = "alexei.chen@ysu.edu.cn",
                fileName = "China_Work_Permit_Category_B_Policy.txt",
                fileType = "TXT",
                category = "CAREER",
                uploadDate = "2026-09-15",
                tags = "work permit, immigration, category b, guidelines, nia",
                userNotes = "Official 2025/2026 regulations downloaded from Ministry of Human Resources.",
                courseOrProject = "Career Planning",
                content = "Official Policy: Foreigner's Work Permit in China (Category B & A)\nSource: National Immigration Administration & Ministry of Human Resources.\nKey Provisions:\n1. Master's/PhD degree graduates from recognized Chinese universities are directly eligible for Category B Work Permit without the previous 2-year overseas work experience requirement.\n2. Bachelor's graduates must achieve >= 60 points on the Evaluation Rubric or be hired by enterprises in designated National High-Tech Industrial Development Zones.\n3. Scoring criteria: Degree (up to 50 pts), HSK 5 certification (+5-10 pts), salary tier (+10-20 pts), youth age bracket (+10 pts)."
            ),
            PersonalDocumentEntity(
                userEmail = "alexei.chen@ysu.edu.cn",
                fileName = "YSU_International_Student_ISO_Procedures.txt",
                fileType = "TXT",
                category = "UNIVERSITY",
                uploadDate = "2026-09-02",
                tags = "residence permit, registration, iso, campus card, police",
                userNotes = "Guidelines from International Education College building.",
                courseOrProject = "Yanshan University",
                content = "Yanshan University International Student Office Guidelines:\n1. Accommodation Registration: Off-campus residents must register at the local police station (派出所) within 24 hours of moving.\n2. Residence Permit Renewal: Submit passport, enrollment letter, and physical exam verification to ISO at least 30 days prior to permit expiry.\n3. Campus Smart Card: Recharges done through WeChat Pay or Perfect Campus app."
            ),
            PersonalDocumentEntity(
                userEmail = "alexei.chen@ysu.edu.cn",
                fileName = "Technical_CS_Chinese_Vocabulary.md",
                fileType = "MD",
                category = "CHINESE",
                uploadDate = "2026-09-12",
                tags = "vocabulary, academic chinese, machine learning, defense",
                userNotes = "Personal study notes for lab meetings and thesis defense.",
                courseOrProject = "CH302: Technical Chinese",
                content = "# Technical CS Chinese Vocabulary\n- 卷积神经网络 (juǎn jī shén jīng wǎng luò): Convolutional Neural Network\n- 损失函数 (sǔn shī hán shù): Loss Function\n- 显存溢出 (xiǎn cún yì chū): CUDA Out of Memory\n- 导师 (dǎo shī): Academic Advisor\n- 答辩 (dá biàn): Oral Defense\n- 调优 (tiáo yōu): Optimization / Tuning"
            )
        )
        db.personalDocumentDao().insertAll(initialDocs)

        // 10. Stage 4 Career & Job Assistant Initial Data
        val careerProfile = CareerProfileEntity(
            id = 1,
            degree = "Bachelor of Engineering in CS & Technology (计算机科学与技术)",
            university = "Yanshan University (燕山大学)",
            department = "School of Information Science & Engineering",
            graduationDate = "June 2027",
            researchAreas = "Computer Vision, 3D LiDAR Perception, Deep Learning",
            programmingLanguages = "Python, C++, C, Java, SQL",
            frameworks = "PyTorch, TorchVision, Open3D, NumPy, SciPy",
            aiMlSkills = "Supervised Learning, Semi-Supervised Learning, Loss Formulation, Attention Mechanisms",
            cvSkills = "3D Point Cloud Segmentation, Object Detection, Depth Estimation, Optical Backscattering Models",
            seSkills = "Git, Linux, OOP, Design Patterns, Data Structures & Algorithms, Valgrind, GDB",
            databases = "SQLite, PostgreSQL, MySQL",
            cloud = "Docker, Linux Server Administration, Remote GPU Cluster SSH",
            dataScience = "Pandas, Matplotlib, Seaborn, Evaluation Metrics (mIoU, Precision, Recall, F1)",
            researchSkills = "LaTeX, Paper Writing, Ablation Design, Empirical Benchmarking, Literature Matrix Synthesis",
            publications = "Robust Semi-Supervised 3D LiDAR Semantic Segmentation in Maritime Coastal Fog (CVPR 2027 Submission)",
            projects = "PointFog-SAM, xv6 OS Kernel COW Fork, YSU Timetable AI Parser",
            internships = "Autonomous Driving Lab Research Intern (Yanshan University AI Lab)",
            workExperience = "Graduate Research Assistant (Perception Lab, YSU)",
            certifications = "NVIDIA Deep Learning Institute (DLI) Fundamentals, CS61A/B Completion",
            githubUrl = "https://github.com/alexei-ysu-cs",
            portfolioUrl = "https://alexei-scholar.dev",
            linkedinUrl = "https://linkedin.com/in/alexei-chen-kovalenko",
            englishProficiency = "Fluent / Professional Working (IELTS 7.5 equivalent)",
            chineseProficiency = "HSK 4 Certified (320 words mastered, preparing HSK 5)",
            targetJobTitles = "Computer Vision Engineer, AI Algorithm Engineer, Machine Learning Engineer, AI Researcher",
            targetCountries = "China, Singapore, Japan, Europe, Remote",
            targetCities = "Beijing, Shanghai, Shenzhen, Hangzhou, Singapore, Berlin, Tokyo",
            targetIndustries = "Autonomous Driving, Robotics, AI R&D, Intelligent Port Logistics"
        )
        db.careerDao().insertCareerProfile(careerProfile)

        // Career Goals
        val careerGoals = listOf(
            CareerGoalEntity(
                targetRole = "Computer Vision / AI Algorithm Engineer",
                country = "China",
                city = "Beijing / Shanghai",
                industry = "Autonomous Driving & Robotics",
                desiredExperienceLevel = "Entry-Level / Master Graduate",
                targetApplicationDate = "Spring 2027",
                isPrimary = true,
                notes = "Primary target in autonomous commercial port vehicles (e.g. Qinhuangdao / Ningbo)."
            ),
            CareerGoalEntity(
                targetRole = "Machine Learning Systems Engineer",
                country = "Singapore",
                city = "Singapore",
                industry = "AI Infrastructure",
                desiredExperienceLevel = "Entry-Level",
                targetApplicationDate = "Fall 2027",
                isPrimary = false,
                notes = "Global R&D team focus."
            ),
            CareerGoalEntity(
                targetRole = "AI Research Scientist / PhD Researcher",
                country = "China",
                city = "Beijing",
                industry = "Top Academy & University Labs",
                desiredExperienceLevel = "PhD Candidate",
                targetApplicationDate = "Late 2027",
                isPrimary = false,
                notes = "Continuation of 3D point cloud adverse weather perception line."
            )
        )
        db.careerDao().insertCareerGoals(careerGoals)

        // Skill Inventory (strictly with verified evidence)
        val skillInventory = listOf(
            SkillInventoryEntity(skillName = "Python", category = "Languages", currentLevel = "Advanced", evidence = "Engineered PointFog-SAM codebase, PyTorch autograd loss functions, PyTorch Geometric.", lastPracticed = "2026-10-01", relatedProject = "PointFog-SAM"),
            SkillInventoryEntity(skillName = "C++", category = "Languages", currentLevel = "Intermediate", evidence = "Implemented xv6 user programs, modern C++17 pointers, OpenCV C++ bindings.", lastPracticed = "2026-09-28", relatedProject = "xv6 Memory COW"),
            SkillInventoryEntity(skillName = "PyTorch", category = "AI/ML", currentLevel = "Advanced", evidence = "Trained DDP distributed models on NVIDIA RTX 4090 cluster, custom loss equations.", lastPracticed = "2026-10-01", relatedProject = "PointFog-SAM"),
            SkillInventoryEntity(skillName = "3D Point Clouds", category = "Computer Vision", currentLevel = "Expert", evidence = "Published CVPR submission benchmarking PointNeXt, Cylinder3D, Hesai Pandar64 LiDAR.", lastPracticed = "2026-10-01", relatedProject = "PointFog-SAM"),
            SkillInventoryEntity(skillName = "Linux & Shell", category = "Tools", currentLevel = "Advanced", evidence = "Daily development in Ubuntu 22.04 LTS, Bash automation, CMake, systemd.", lastPracticed = "2026-10-01", relatedProject = "Lab Workstation"),
            SkillInventoryEntity(skillName = "Git", category = "Tools", currentLevel = "Advanced", evidence = "Multi-branch git workflow, pull requests, semantic versioning.", lastPracticed = "2026-10-01", relatedProject = "GitHub Repositories"),
            SkillInventoryEntity(skillName = "Docker", category = "Tools", currentLevel = "Intermediate", evidence = "Authored reproducible GPU CUDA Dockerfiles for lab paper releases.", lastPracticed = "2026-09-20", relatedProject = "PointFog-SAM"),
            SkillInventoryEntity(skillName = "TensorRT", category = "AI/ML", currentLevel = "Beginner", evidence = "Completed basic ONNX export; developing custom TensorRT plugins.", lastPracticed = "2026-09-15", relatedProject = "Jetson Orin Deployment"),
            SkillInventoryEntity(skillName = "CUDA", category = "Languages", currentLevel = "Beginner", evidence = "Studying warp divergence and thread tiling on NVIDIA DLI courses.", lastPracticed = "2026-09-10", relatedProject = "CUDA Lab Tutorials")
        )
        db.careerDao().insertSkills(skillInventory)

        // Verified Job Postings
        val jobPostings = listOf(
            JobPostingEntity(
                jobTitle = "Computer Vision Algorithm Engineer (3D Perception)",
                company = "Haomo.ai (毫末智行)",
                location = "Beijing (Haidian / Yizhuang)",
                country = "China",
                remotePolicy = "On-site",
                industry = "Autonomous Driving",
                jobType = "Full-time",
                experienceLevel = "Entry-Level / Master Graduate",
                requiredDegree = "Master's or Bachelor's in CS / Automation",
                requiredExperience = "0-2 years (Lab/Internship experience accepted)",
                requiredSkills = "Python, C++, PyTorch, 3D Point Clouds, Deep Learning, Linux, Git",
                preferredSkills = "CUDA, TensorRT, ROS2, Published papers in CVPR/ICCV",
                programmingLanguages = "Python, C++",
                frameworks = "PyTorch, TensorRT",
                tools = "Linux, Git, Docker",
                researchRequirements = "Experience with LiDAR point cloud segmentation or multi-sensor fusion.",
                languageRequirements = "Working English; conversational Chinese is preferred.",
                responsibilities = "Develop 3D semantic segmentation models for commercial logistics delivery vehicles.",
                applicationDeadline = "2027-04-15",
                salary = "¥28,000 - ¥40,000 / month + annual bonus",
                source = "Official Campus Recruitment Portal (Verified)",
                dateChecked = "2026-10-01",
                applicationUrl = "https://careers.haomo.ai/jobs/cv-algo-2027",
                isVerified = true,
                isSaved = true
            ),
            JobPostingEntity(
                jobTitle = "Autonomous Driving Perception Engineer",
                company = "Momenta (初速度)",
                location = "Suzhou / Shanghai",
                country = "China",
                remotePolicy = "Hybrid",
                industry = "Autonomous Driving",
                jobType = "Full-time",
                experienceLevel = "Junior / New Graduate",
                requiredDegree = "Master's or Bachelor's in CS",
                requiredExperience = "Demonstrated deep learning project portfolio",
                requiredSkills = "Python, PyTorch, C++, Computer Vision, Linux",
                preferredSkills = "Model Quantization, ONNX, CUDA, Git",
                programmingLanguages = "Python, C++",
                frameworks = "PyTorch, OpenCV",
                tools = "Linux, Git",
                researchRequirements = "Strong mathematical foundation in linear algebra and neural optimization.",
                languageRequirements = "Bilingual English/Chinese collaboration.",
                responsibilities = "Train and deploy deep vision transformer networks for mass-production vehicles.",
                applicationDeadline = "2027-05-01",
                salary = "¥30,000 - ¥45,000 / month",
                source = "Momenta Career Site (Verified)",
                dateChecked = "2026-10-01",
                applicationUrl = "https://momenta.cn/join-us",
                isVerified = true,
                isSaved = true
            ),
            JobPostingEntity(
                jobTitle = "AI Research Fellow (Computer Vision)",
                company = "Institute for Infocomm Research (I2R, A*STAR)",
                location = "Singapore (Fusionopolis)",
                country = "Singapore",
                remotePolicy = "On-site",
                industry = "National Research Institute",
                jobType = "Full-time",
                experienceLevel = "Master Graduate / PhD",
                requiredDegree = "Master's or PhD in Computer Science",
                requiredExperience = "Track record of scientific publication",
                requiredSkills = "Python, PyTorch, Deep Learning, Scientific Writing, LaTeX",
                preferredSkills = "Multimodal models, Foundation Models, 3D Vision",
                programmingLanguages = "Python",
                frameworks = "PyTorch",
                tools = "Linux, Git",
                researchRequirements = "First-author paper at top AI conference.",
                languageRequirements = "Fluent English.",
                responsibilities = "Investigate next-generation robust visual perception models under environmental degradation.",
                applicationDeadline = "2027-06-30",
                salary = "SGD 6,500 - 8,500 / month",
                source = "A*STAR Official Careers (Verified)",
                dateChecked = "2026-10-01",
                applicationUrl = "https://www.a-star.edu.sg/careers",
                isVerified = true,
                isSaved = false
            )
        )
        db.careerDao().insertJobPostings(jobPostings)

        // Skill Development Plans
        val skillPlans = listOf(
            SkillDevelopmentPlanEntity(
                skillName = "TensorRT C++ Inference Engine",
                currentLevel = "Beginner",
                targetLevel = "Advanced",
                whyItMatters = "Required for 80% of autonomous driving R&D deployment roles on Jetson Orin.",
                learningResources = "NVIDIA TensorRT Developer Guide, TensoRT OSS GitHub samples.",
                practiceTask = "Write custom IPluginV2DynamicExt layer for Koschmieder scattering inversion.",
                suggestedProject = "PointFog-SAM edge inference optimization",
                targetDate = "2026-11-30",
                status = "In Progress"
            ),
            SkillDevelopmentPlanEntity(
                skillName = "Modern C++ (C++17/20 & STL Concurrency)",
                currentLevel = "Intermediate",
                targetLevel = "Advanced",
                whyItMatters = "Core requirement for robotics runtime pipelines (ROS2 nodes).",
                learningResources = "Effective Modern C++ by Scott Meyers, cppreference.com.",
                practiceTask = "Build a lock-free ring buffer queue for incoming LiDAR packet sweeps.",
                suggestedProject = "LiDAR-Sweep-Driver C++ Daemon",
                targetDate = "2026-12-15",
                status = "In Progress"
            )
        )
        db.careerDao().insertSkillPlans(skillPlans)

        // Job Applications
        val applications = listOf(
            JobApplicationEntity(
                company = "Haomo.ai (毫末智行)",
                position = "Computer Vision Algorithm Engineer",
                location = "Beijing",
                applicationUrl = "https://careers.haomo.ai/jobs/cv-algo-2027",
                dateApplied = "2026-09-28",
                status = "Preparing",
                interviewDate = "TBD (Spring 2027 Campus Window)",
                followUpDate = "2026-11-01",
                notes = "Tailoring CV with PointFog-SAM CVPR submission metrics.",
                jobDescription = "3D semantic segmentation for autonomous delivery vehicles.",
                cvVersionUsed = "CV_Alexei_Chen_Kovalenko_2027_CVPR.pdf"
            ),
            JobApplicationEntity(
                company = "Momenta (初速度)",
                position = "Perception Algorithm Engineer",
                location = "Suzhou / Shanghai",
                applicationUrl = "https://momenta.cn/join-us",
                dateApplied = "2026-09-30",
                status = "Saved",
                interviewDate = "",
                followUpDate = "2026-10-25",
                notes = "Saved for campus recruitment window opening.",
                jobDescription = "Mass-production vision transformer models.",
                cvVersionUsed = "CV_Alexei_Chen_Kovalenko_2027_CVPR.pdf"
            )
        )
        db.careerDao().insertApplications(applications)

        // Interview Prep Questions (Project-Grounded)
        val interviewQuestions = listOf(
            InterviewPrepQuestionEntity(
                category = "Computer Vision",
                targetRoleOrProject = "PointFog-SAM Research",
                question = "In your PointFog-SAM project, how did you model the atmospheric attenuation of LiDAR beams in coastal fog?",
                userAnswer = "We leveraged Koschmieder's exponential extinction law: intensity diminishes with distance according to exp(-beta * d). We formulated a differentiable depth-aware compensation layer in PyTorch that inverted this decay prior to the attention backbone, boosting mIoU by +8.4% on real Qinhuangdao port fog.",
                aiFeedback = "Concrete technical strength: explicitly cites Koschmieder's optical formula, implementation mechanism (differentiable layer in PyTorch), and quantitative benchmark (+8.4% mIoU on real port scans).",
                improvedAnswer = "Strong response. To make it exceptional, add the specific compute overhead (e.g. 'introduces only 1.2M parameters (<3% overhead) maintaining 38 FPS on NVIDIA Jetson AGX Orin').",
                isPracticed = true
            ),
            InterviewPrepQuestionEntity(
                category = "Research",
                targetRoleOrProject = "PointFog-SAM Research",
                question = "How did you prevent pseudo-cluster noise from corrupting semantic segmentation in your maritime port dataset?",
                userAnswer = "Suspended water droplets produce spurious backscatter returns. We introduced cross-sweep spatio-temporal consistency across consecutive LiDAR scans, filtering transient droplet clusters that lack temporal rigidity.",
                aiFeedback = "Clear explanation of the physical failure mode and the temporal consistency regularization rationale.",
                improvedAnswer = "Elaborate briefly on the threshold selection: 'We tuned the temporal overlap threshold to 0.72 based on sequence-isolated cross-validation.'",
                isPracticed = true
            ),
            InterviewPrepQuestionEntity(
                category = "Coding",
                targetRoleOrProject = "xv6 Memory COW Fork",
                question = "Explain how you implemented page-fault based Copy-on-Write (COW) fork in the xv6 operating system kernel.",
                userAnswer = "In fork(), instead of allocating fresh physical memory with copyuvm(), we mapped parent pages read-only to the child and set a PTE_COW bit in the page table flags while incrementing an atomic physical page reference counter. When either process attempts to write, a page fault (T_PGFLT) is triggered in usertrap(). We allocate a single page, copy the 4096 bytes, remap with write permissions, and decrement the ref count.",
                aiFeedback = "Excellent kernel-level specificity. Mentions exact page table flags (PTE_COW), trap handler (usertrap, T_PGFLT), reference counting, and 4KB page granularity.",
                improvedAnswer = "Address the race condition boundary: explain how you protected the reference counter with a spinlock during concurrent SMP traps.",
                isPracticed = true
            )
        )
        db.careerDao().insertInterviewQuestions(interviewQuestions)

        // Career Roadmap Milestones
        val roadmapMilestones = listOf(
            CareerRoadmapMilestoneEntity(stage = "Skills", title = "Complete CUDA & TensorRT Edge Inference Optimization", targetDate = "Nov 30, 2026", isCompleted = false, orderIndex = 1, notes = "Benchmarked on Jetson Orin at >30 FPS."),
            CareerRoadmapMilestoneEntity(stage = "Projects", title = "Release PointFog-SAM Open-Source PyTorch Benchmark & GitHub", targetDate = "Dec 15, 2026", isCompleted = false, orderIndex = 2, notes = "Clean conda environment, Dockerfile, pre-trained weights."),
            CareerRoadmapMilestoneEntity(stage = "Experience", title = "Present Coastal Port LiDAR Findings to YSU Lab Seminar", targetDate = "Dec 20, 2026", isCompleted = false, orderIndex = 3, notes = "Feedback from Prof. Zhang Lin."),
            CareerRoadmapMilestoneEntity(stage = "CV", title = "Finalize English & Chinese Bilingual Technical Resumes", targetDate = "Jan 10, 2027", isCompleted = false, orderIndex = 4, notes = "Include CVPR 2027 submission and GitHub links."),
            CareerRoadmapMilestoneEntity(stage = "Interviews", title = "Complete 50 LeetCode Mediums & 20 3D CV Technical Mock Interviews", targetDate = "Feb 20, 2027", isCompleted = false, orderIndex = 5, notes = "Focus on PointNet, IoU loss, CUDA kernels, OS memory."),
            CareerRoadmapMilestoneEntity(stage = "Applications", title = "Submit Spring 2027 Campus Applications to Haomo.ai, Momenta & Horizon", targetDate = "Mar 01, 2027", isCompleted = false, orderIndex = 6, notes = "Early campus recruitment window.")
        )
        db.careerDao().insertRoadmapMilestones(roadmapMilestones)

        // ==========================================
        // 7. STAGE 5: CHINESE LANGUAGE COACH SEED DATA
        // ==========================================

        val email = "alexei.chen@ysu.edu.cn"

        // 7.1 Chinese Language Profile
        val chineseProfile = ChineseLanguageProfileEntity(
            id = 1,
            userEmail = email,
            currentLevel = "Intermediate (HSK 4)",
            hskLevel = "HSK 4",
            targetHskLevel = "HSK 5",
            learningGoal = "Academic lab meetings, thesis defense in Chinese, everyday campus & hospital/banking fluency",
            dailyStudyMinutes = 30,
            preferredStudyTime = "Morning (08:00 - 08:30)",
            daysPerWeek = 5,
            targetDate = "2027-06-30",
            speakingConfidence = 2,
            listeningConfidence = 3,
            readingConfidence = 4,
            writingConfidence = 2,
            vocabularyConfidence = 3,
            grammarConfidence = 3,
            currentStreak = 7,
            longestStreak = 14,
            daysStudiedCount = 28,
            weeklyGoalMinutes = 150,
            notificationsEnabled = true
        )
        db.chineseDao().insertOrUpdateProfile(chineseProfile)

        // 7.2 Spaced Repetition Vocabulary
        val vocabularyItems = listOf(
            ChineseVocabularyEntity(
                userEmail = email,
                hanzi = "点云语义分割",
                pinyin = "diǎn yún yǔ yì fēn gē",
                english = "Point Cloud Semantic Segmentation",
                partOfSpeech = "Technical Term",
                exampleSentenceZh = "我们的实验评估了模型在复杂海港点云语义分割上的表现。",
                exampleSentenceEn = "Our experiments evaluated the model's performance on complex seaport point cloud semantic segmentation.",
                userNotes = "Key term for YSU thesis defense.",
                difficulty = "Medium",
                hskLevel = "HSK 5",
                category = "Computer Science",
                source = "Research Lab: EXP-002",
                isFavorite = true,
                isKnown = true,
                reviewStatus = "Mastered",
                reviewCount = 4,
                correctAnswers = 4,
                incorrectAnswers = 0,
                intervalDays = 21,
                nextReviewDate = "2026-10-23"
            ),
            ChineseVocabularyEntity(
                userEmail = email,
                hanzi = "显存溢出",
                pinyin = "xiǎn cún yì chū",
                english = "CUDA Out of Memory (OOM)",
                partOfSpeech = "Technical Term",
                exampleSentenceZh = "Batch size 设置过大会导致显存溢出，建议开启混合精度训练。",
                exampleSentenceEn = "Setting the batch size too large causes CUDA out of memory; mixed precision is recommended.",
                userNotes = "Lab jargon.",
                difficulty = "Easy",
                hskLevel = "HSK 4",
                category = "Computer Science",
                source = "Research Lab",
                isFavorite = false,
                isKnown = true,
                reviewStatus = "Mastered",
                reviewCount = 3,
                correctAnswers = 3,
                incorrectAnswers = 0,
                intervalDays = 14,
                nextReviewDate = "2026-10-16"
            ),
            ChineseVocabularyEntity(
                userEmail = email,
                hanzi = "消融实验",
                pinyin = "xiāo róng shí yàn",
                english = "Ablation Study",
                partOfSpeech = "Noun",
                exampleSentenceZh = "消融实验证实了散射逆变换模块的性能贡献。",
                exampleSentenceEn = "The ablation study verified the performance contribution of the scattering inversion module.",
                userNotes = "Review before lab group presentation.",
                difficulty = "Hard",
                hskLevel = "HSK 5",
                category = "Academic Research",
                source = "Research Lab",
                isFavorite = true,
                isKnown = false,
                reviewStatus = "Due",
                reviewCount = 1,
                correctAnswers = 1,
                incorrectAnswers = 1,
                intervalDays = 1,
                nextReviewDate = "2026-10-03"
            ),
            ChineseVocabularyEntity(
                userEmail = email,
                hanzi = "在读证明",
                pinyin = "zài dú zhèng míng",
                english = "Certificate of Enrollment",
                partOfSpeech = "Noun",
                exampleSentenceZh = "请问在读证明需要到国际教育学院哪个窗口盖章？",
                exampleSentenceEn = "Which window at the College of International Education stamps the enrollment certificate?",
                userNotes = "Required for residence permit renewal.",
                difficulty = "Medium",
                hskLevel = "HSK 4",
                category = "University",
                source = "University Administration",
                isFavorite = false,
                isKnown = false,
                reviewStatus = "Due",
                reviewCount = 1,
                correctAnswers = 0,
                incorrectAnswers = 1,
                intervalDays = 1,
                nextReviewDate = "2026-10-03"
            ),
            ChineseVocabularyEntity(
                userEmail = email,
                hanzi = "居留许可",
                pinyin = "jū liú xǔ kě",
                english = "Residence Permit",
                partOfSpeech = "Noun",
                exampleSentenceZh = "居留许可到期前三十天必须向出入境管理局提交延期申请。",
                exampleSentenceEn = "You must submit an extension application to the Exit-Entry Administration 30 days before permit expiry.",
                userNotes = "Legal compliance: Article 39.",
                difficulty = "Medium",
                hskLevel = "HSK 4",
                category = "Police/administration",
                source = "Police Notice",
                isFavorite = true,
                isKnown = true,
                reviewStatus = "Upcoming",
                reviewCount = 2,
                correctAnswers = 2,
                incorrectAnswers = 0,
                intervalDays = 7,
                nextReviewDate = "2026-10-09"
            ),
            ChineseVocabularyEntity(
                userEmail = email,
                hanzi = "延期",
                pinyin = "yán qī",
                english = "Postpone / Extend Deadline",
                partOfSpeech = "Verb",
                exampleSentenceZh = "因实验尚未跑完，学生冒昧申请将大作业提交时间延期两天。",
                exampleSentenceEn = "As the experiment has not finished running, I humbly request a 2-day extension on the assignment.",
                userNotes = "Extracted from syllabus document.",
                difficulty = "Medium",
                hskLevel = "HSK 4",
                category = "Classroom",
                source = "University Notice",
                isFavorite = false,
                isKnown = false,
                reviewStatus = "Due",
                reviewCount = 2,
                correctAnswers = 1,
                incorrectAnswers = 1,
                intervalDays = 1,
                nextReviewDate = "2026-10-03"
            ),
            ChineseVocabularyEntity(
                userEmail = email,
                hanzi = "校园一卡通",
                pinyin = "xiào yuán yī kǎ tōng",
                english = "Campus Smart Card",
                partOfSpeech = "Noun",
                exampleSentenceZh = "校园一卡通可以在食堂刷卡用餐和宿舍洗澡用热水。",
                exampleSentenceEn = "The campus card is used for dining at canteens and hot water in dorm showers.",
                userNotes = "East Campus utilities.",
                difficulty = "Easy",
                hskLevel = "HSK 3",
                category = "Dormitory",
                source = "Campus Guide",
                isFavorite = false,
                isKnown = true,
                reviewStatus = "Mastered",
                reviewCount = 5,
                correctAnswers = 5,
                incorrectAnswers = 0,
                intervalDays = 30,
                nextReviewDate = "2026-10-31"
            )
        )
        db.chineseDao().insertAllVocabulary(vocabularyItems)

        // 7.3 Daily Chinese Study Sessions (Synchronized with Study Planner)
        val initialChineseSessions = listOf(
            ChineseStudySessionEntity(
                userEmail = email,
                dayOfWeek = "Monday",
                timeSlot = "08:00 - 08:30",
                moduleType = "Vocabulary & SRS",
                title = "HSK 4/5 Academic & CS Vocabulary (15 min)",
                description = "Clear 10 'Due' flashcards and review point cloud terminology.",
                durationMinutes = 15,
                isCompleted = true
            ),
            ChineseStudySessionEntity(
                userEmail = email,
                dayOfWeek = "Monday",
                timeSlot = "08:15 - 08:30",
                moduleType = "Grammar",
                title = "Grammar: 随着...的发展 (15 min)",
                description = "Practice formal thesis transition connectors.",
                durationMinutes = 15,
                isCompleted = true
            ),
            ChineseStudySessionEntity(
                userEmail = email,
                dayOfWeek = "Tuesday",
                timeSlot = "08:00 - 08:30",
                moduleType = "Listening",
                title = "Lab Seminar & Canteen Dialogue Listening (30 min)",
                description = "Listen to lab progress dialogue and answer 3 comprehension questions without pinyin.",
                durationMinutes = 30,
                isCompleted = false
            ),
            ChineseStudySessionEntity(
                userEmail = email,
                dayOfWeek = "Wednesday",
                timeSlot = "08:00 - 08:30",
                moduleType = "Speaking",
                title = "Spoken Dialogue: Office Hour with Professor (30 min)",
                description = "Practice respectful honorifics and speech transcription comparison.",
                durationMinutes = 30,
                isCompleted = false
            ),
            ChineseStudySessionEntity(
                userEmail = email,
                dayOfWeek = "Thursday",
                timeSlot = "08:00 - 08:30",
                moduleType = "Reading & Writing",
                title = "Formal Email Drafting & Reading (30 min)",
                description = "Draft respectful leave/extension email using '尊敬的张老师' and '顺祝教安'.",
                durationMinutes = 30,
                isCompleted = false
            ),
            ChineseStudySessionEntity(
                userEmail = email,
                dayOfWeek = "Friday",
                timeSlot = "08:00 - 08:30",
                moduleType = "Review",
                title = "Weekly Review & Weakness Check (30 min)",
                description = "Review all difficult vocabulary and run factual learning weakness analyzer.",
                durationMinutes = 30,
                isCompleted = false
            )
        )
        db.chineseDao().insertAllStudySessions(initialChineseSessions)

        // 7.4 Listening Exercises
        val listeningExercises = listOf(
            ChineseListeningExerciseEntity(
                userEmail = email,
                title = "Yanshan University Lab Group Meeting Notice",
                category = "Academic & Research",
                chineseText = "各位同学请注意：本周五下午两点半在东校区信息馆 402 会议室召开实验室组会。请每位硕士生准备五分钟的项目实验进展汇报，重点汇报模型在测试集上的指标变化。",
                pinyinText = "Gèwèi tóngxué qǐng zhùyì: Běn zhōuwǔ xiàwǔ liǎng diǎn bàn zài dōng xiàoqū xìnxī guǎn 402 huìyìshì zhàokāi shíyànshì zǔhuì. Qǐng měi wèi shuòshìshēng zhǔnbèi wǔ fēnzhōng de xiàngmù shíyàn jìnzhǎn huìbào, zhòngdiǎn huìbào móxíng zài cèshìjí shàng de zhǐbiāo biànhuà.",
                englishTranslation = "Attention everyone: This Friday at 2:30 PM, the lab group meeting will be held in Room 402, Information Science Hall, East Campus. Master's students must prepare a 5-minute project progress presentation, focusing on metric changes on the test set.",
                question = "组会的时间和地点在哪里？(When and where is the lab group meeting?)",
                optionsJson = "A) 周五下午两点半，东校区信息馆 402|B) 周四上午九点，西校区图书馆|C) 周五上午十点，留学生公寓大厅|D) 周日晚上七点，第一食堂三楼",
                correctOptionIndex = 0,
                explanation = "The audio explicitly states '本周五下午两点半' (Friday 2:30 PM) at '东校区信息馆 402 会议室' (East Campus Information Science Hall, Room 402)."
            ),
            ChineseListeningExerciseEntity(
                userEmail = email,
                title = "Campus Card Water Meter Troubleshooting",
                category = "Campus Life",
                chineseText = "同学你好，宿舍洗澡间的热水表如果显示错误代码 E-02，通常是因为卡内热水余额不足或者读卡芯片接触不良。你可以先用完美校园 APP 重新充值，然后把卡贴在水表感应区保持三秒钟。",
                pinyinText = "Tóngxué nǐ hǎo, sùshè xǐzǎojiān de rèshuǐ biǎo rúguǒ xiǎnshì cuòwù dàimǎ E-02, tōngcháng shì yīnwèi kǎ nèi rèshuǐ yú'é bùzú huòzhě dúkǎ xīnpiàn jiēchù bùliáng. Nǐ kěyǐ xiān yòng Wánměi Xiàoyuán APP chóngxīn chōngzhí, ránhòu bǎ kǎ tiē zài shuǐbiǎo gǎnyìng qū bǎochí sān miǎozhōng.",
                englishTranslation = "Hello student, if the hot water meter in the dormitory shower displays error code E-02, it is usually because the hot water balance is insufficient or the smart card chip has poor contact. You can recharge via the Wanmei Xiaoyuan app, then hold the card against the sensor for 3 seconds.",
                question = "水表显示 E-02 错误的主要原因是什么？(What is the main reason for error code E-02?)",
                optionsJson = "A) 水管破裂漏水|B) 热水余额不足或读卡不良|C) 宿舍水闸关闭|D) 超过晚上十点断水",
                correctOptionIndex = 1,
                explanation = "The speaker notes that E-02 means '卡内热水余额不足或者读卡芯片接触不良' (insufficient balance or poor card chip contact)."
            ),
            ChineseListeningExerciseEntity(
                userEmail = email,
                title = "Temporary Residence Registration Inquiry",
                category = "Police & Legal",
                chineseText = "派出所民警提醒：外国留学生如果在校外租房居住，必须在入住后二十四小时之内，由本人或房东携带租房合同、房产证复印件以及护照原件，前往辖区派出所户籍窗口办理境外人员住宿登记表。",
                pinyinText = "Pàichūsuǒ mínjǐng tíxǐng: Wàiguó liúxuéshēng rúguǒ zài xiàowài zūfáng jūzhù, bìxū zài rùzhù hòu èrshísì xiǎoshí zhīnèi, yóu běnrén huò fángdōng xiédài zūfáng hétong, fángchǎnzhèng fùyìnjiàn yǐjí hùzhào yuánjiàn, qiánwǎng xiáqū pàichūsuǒ hùjí chuāngkǒu bànlǐ jìngwài rényuán zhùsù dēngjì biǎo.",
                englishTranslation = "Police station reminder: Foreign international students renting off-campus must, within 24 hours of moving in, bring the lease contract, copy of property deed, and original passport to the jurisdiction police station counter to register temporary residence.",
                question = "外国留学生校外租房必须在多长时间内办理住宿登记？(Within what time must students register?)",
                optionsJson = "A) 三天内|B) 一周内|C) 二十四小时内|D) 一个月内",
                correctOptionIndex = 2,
                explanation = "Chinese entry-exit administration law mandates registration within '二十四小时之内' (24 hours)."
            )
        )
        db.chineseDao().insertAllListeningExercises(listeningExercises)

        // 7.5 Core Grammar Points
        val grammarPoints = listOf(
            ChineseGrammarPointEntity(
                hskLevel = "HSK 3",
                pattern = "把 (bǎ) 字句 — Disposal Structure",
                explanation = "Used when an action produces a result, relocation, or change of state on a specific definite object: Subject + 把 + Object + Verb + Other Element/Result.",
                exampleZh = "请大家把实验代码上传到 GitHub 仓库中。",
                examplePinyin = "Qǐng dàjiā bǎ shíyàn dàimǎ shàngchuán dào GitHub cāngkù zhōng.",
                exampleEn = "Please upload the experimental code into the GitHub repository.",
                commonMistakes = "Incorrect: 我把代码写。(Incomplete verb). Must have a result/direction complement, e.g., 写完了 / 上传了.",
                practicePrompt = "Transform into 把 sentence: '请提交报告给张老师。' -> '请把报告提交给张老师。'"
            ),
            ChineseGrammarPointEntity(
                hskLevel = "HSK 4",
                pattern = "不仅...而且... (bùjǐn... érqiě...) — Progressive Conjunction",
                explanation = "Expresses 'not only... but also...', adding a higher level of significance or additional capability.",
                exampleZh = "该模型不仅降低了推理延时，而且在雾天场景下的分割精度显著提高。",
                examplePinyin = "Gāi móxíng bùjǐn jiàngdī le tuīlǐ yánshí, érqiě zài wùtiān chǎngjǐng xià de fēn'gē jīngdù xiǎnzhù tígāo.",
                exampleEn = "This model not only reduced inference latency, but also significantly improved segmentation accuracy in fog scenes.",
                commonMistakes = "Placing 不仅 after the subject when subjects are different. If subjects are different, 不仅 comes before the first subject.",
                practicePrompt = "Connect: Python 很简单 (easy) + 库很丰富 (rich libraries)."
            ),
            ChineseGrammarPointEntity(
                hskLevel = "HSK 5",
                pattern = "随着...的发展 (suízhe... de fāzhǎn) — Temporal / Evolutionary Framing",
                explanation = "Classic formal academic paper opener: 'With the development of X, Y has become...'",
                exampleZh = "随着自动驾驶与车载传感器技术的发展，恶劣天气下的点云鲁棒感知成为学术界的研究热点。",
                examplePinyin = "Suízhe zìdòng jiǎishǐ yǔ chēzài chuángǎnqì jìshù de fāzhǎn, èliè tiānqì xià de diǎnyún lǔbàng gǎnzhī chéngwéi xuéshùjiè de yánjiū rèdiǎn.",
                exampleEn = "With the development of autonomous driving and vehicular sensors, robust point cloud perception in adverse weather has become an academic research focus.",
                commonMistakes = "Confusing 随着 (along with) with 按照 (according to).",
                practicePrompt = "Draft the opening sentence of your master's thesis introduction using 随着."
            )
        )
        db.chineseDao().insertAllGrammarPoints(grammarPoints)

        // 7.6 Weekly Progress Tracker
        val weeklyProgress = ChineseWeeklyProgressEntity(
            userEmail = email,
            weekLabel = "Week 5 (October 2026)",
            studyMinutes = 145,
            wordsReviewed = 68,
            wordsLearned = 24,
            exercisesCompleted = 18,
            listeningPracticeCount = 6,
            speakingPracticeCount = 4,
            readingPracticeCount = 8,
            writingPracticeCount = 3,
            conversationSessionsCount = 5
        )
        db.chineseDao().insertOrUpdateWeeklyProgress(weeklyProgress)

        // ========================================================
        // 8. STAGE 6: CHINA STUDENT -> GRADUATION -> WORK & IMMIGRATION SEED DATA
        // ========================================================

        // 8.1 User Immigration Profile (Source-driven, strictly explicitly provided fields)
        val initialImmigrationProfile = ImmigrationProfileEntity(
            id = 1,
            userEmail = email,
            nationality = "Russian",
            currentUniversity = "Yanshan University (燕山大学)",
            universityCityProvince = "Qinhuangdao, Hebei Province",
            degreeLevel = "Master of Science (M.Sc.)",
            major = "Computer Science and Technology (Computer Vision & AI)",
            currentStudentStatus = "Enrolled Full-time (Year 2)",
            expectedGraduationDate = "2027-06-30",
            currentVisaCategory = "X1 (Initial entry visa)",
            currentResidencePermitType = "Study (学习)",
            residencePermitExpirationDate = "2027-07-15",
            passportExpirationDate = "2029-04-20",
            passportNumberOptional = "Not provided", // Privacy rule: never store or default real passport numbers
            targetEmploymentCity = "Shanghai",
            targetJobField = "Artificial Intelligence / Autonomous Driving Algorithm Engineer",
            targetEmployerType = "High-tech Innovation Enterprise (高新技术企业)",
            intendsToRemainInChina = "Yes (Seeking post-graduation employment in China)",
            isConsideringEntrepreneurship = "Considering tech incubator pathway in AI/Robotics",
            lastUpdated = System.currentTimeMillis()
        )
        db.immigrationDao().insertOrUpdateProfile(initialImmigrationProfile)

        // 8.2 Official Source Registry
        val officialSources = listOf(
            OfficialSourceEntity(
                sourceTitle = "Exit and Entry Administration Law of the People's Republic of China (中华人民共和国出境入境管理法)",
                organization = "National Immigration Administration (国家移民管理局) / State Council",
                url = "https://www.nia.gov.cn/n741440/n741542/c1212876/content.html",
                jurisdiction = "National",
                topic = "Foreigner Residence, Visas & 24h Police Accommodation Registration",
                publicationDate = "2012-06-30",
                lastCheckedDate = "Oct 2026",
                status = "Active & Verified",
                summary = "Primary national statutory framework governing stay, residence, registration within 24 hours, and prohibited illegal employment.",
                contentSnippet = "Article 39: Foreigners staying in domiciles other than hotels shall register with the public security organ within 24 hours. Article 43: Foreigners working without a work permit or residence permit endorsement constitute illegal employment."
            ),
            OfficialSourceEntity(
                sourceTitle = "Evaluation Criteria for Foreigners Employed in China (Trial) (外国人来华工作分类标准(试行))",
                organization = "Ministry of Human Resources and Social Security (MOHRSS) & SAFEA (国家外国专家局)",
                url = "http://www.mohrss.gov.cn",
                jurisdiction = "National",
                topic = "Foreigner Work Permit Classification (Category A, B, C) & Points Matrix",
                publicationDate = "2017-03-28",
                lastCheckedDate = "Oct 2026",
                status = "Active & Verified",
                summary = "Establishes unified national standards for Foreigner Work Permits. High-end talent (Score >= 85), Professional talent (Score >= 60), and quota-managed personnel.",
                contentSnippet = "Evaluates salary, degree, work experience, Chinese language proficiency, age, and designated high-tech encouragement industries."
            ),
            OfficialSourceEntity(
                sourceTitle = "Administrative Measures for Enrollment & Cultivation of International Students (Decree No. 42)",
                organization = "Ministry of Education, Ministry of Foreign Affairs, Ministry of Public Security (教育部、外交部、公安部)",
                url = "http://www.moe.gov.cn/srcsite/A02/s5911/moe_621/201705/t20170516_304735.html",
                jurisdiction = "National",
                topic = "Student Work-Study & Off-Campus Internship Endorsements",
                publicationDate = "2017-03-20",
                lastCheckedDate = "Oct 2026",
                status = "Active & Verified",
                summary = "Article 30 authorizes international students to engage in work-study or off-campus internships provided university consent is secured and the residence permit is officially endorsed with 'Internship'.",
                contentSnippet = "International students may not conduct off-campus work or internships beyond the approved scope, duration, or designated hosting entity."
            ),
            OfficialSourceEntity(
                sourceTitle = "Measures Supporting Outstanding Foreign Graduates Working in Shanghai (关于进一步支持外籍人才在沪创新创业的若干措施)",
                organization = "Shanghai Municipal Science and Technology Commission / Shanghai SAFEA (上海市外国专家局)",
                url = "http://stcsm.sh.gov.cn",
                jurisdiction = "Shanghai",
                topic = "Direct Employment for Master's Graduates (2-Year Experience Exemption)",
                publicationDate = "2020-04-10",
                lastCheckedDate = "Oct 2026",
                status = "Active & Verified",
                summary = "Authorizes foreign graduates with Master's degrees or higher from accredited Chinese universities or top 500 world universities to work directly in Shanghai without requiring two years of prior work experience.",
                contentSnippet = "Applies to employment by Shanghai-registered enterprises, specifically prioritizing high-tech zones, Lin-gang Special Area, and strategic emerging industries."
            ),
            OfficialSourceEntity(
                sourceTitle = "Zhongguancun Demonstration Zone International Talent Innovation & Residence Policies",
                organization = "Beijing Municipal Science & Technology Commission / Beijing PSB Exit-Entry Administration",
                url = "http://kw.beijing.gov.cn",
                jurisdiction = "Beijing",
                topic = "Zhongguancun International Student Direct Employment & Entrepreneurship",
                publicationDate = "2019-12-01",
                lastCheckedDate = "Oct 2026",
                status = "Active & Verified",
                summary = "Enables foreign graduates from high-level universities to obtain direct work authorization and entrepreneurship residence permits when hired by or founding technology entities in Zhongguancun.",
                contentSnippet = "Provides streamlined green-channel visa applications and incubation support for STEM master's and doctoral graduates."
            ),
            OfficialSourceEntity(
                sourceTitle = "Yanshan University International Student Status & Exit-Entry Guide (燕山大学留学生学籍与出入境管理规定)",
                organization = "Yanshan University College of International Exchange (燕山大学国际教育学院)",
                url = "http://sie.ysu.edu.cn",
                jurisdiction = "Qinhuangdao / Hebei",
                topic = "Campus Enrollment, Visa Renewals (30-day notice) & Graduation Departure",
                publicationDate = "2024-03-01",
                lastCheckedDate = "Oct 2026",
                status = "Active & Verified",
                summary = "Outlines local university procedures for residence permit extensions, mandatory 30-day renewal timeline, and graduation departure clearance.",
                contentSnippet = "Students must submit renewal materials to the College of International Exchange at least 30 days before permit expiration. Graduation terminates student residence validity upon official reporting to Qinhuangdao PSB."
            ),
            OfficialSourceEntity(
                sourceTitle = "Qinhuangdao Public Security Bureau Foreigner Stay & Residence Guide (秦皇岛市公安局出入境管理支队办事指南)",
                organization = "Qinhuangdao Municipal Public Security Bureau Exit-Entry Administration (秦皇岛市公安局出入境管理支队)",
                url = "http://gat.hebei.gov.cn",
                jurisdiction = "Qinhuangdao / Hebei",
                topic = "Local Exit-Entry Service Hall Procedures & Residence Permits",
                publicationDate = "2021-08-15",
                lastCheckedDate = "Oct 2026",
                status = "Active & Verified",
                summary = "Official service standards and documents for foreign student residence permits, address modification, and stay certificate (停留证件) issuance in Qinhuangdao.",
                contentSnippet = "Requires valid JW202/201 form, official YSU certificate, physical examination report, and accommodation registration slip from the local jurisdiction police station."
            )
        )
        db.immigrationDao().insertOfficialSources(officialSources)

        // 8.3 City Policy Database
        val cityPolicies = listOf(
            CityImmigrationPolicyEntity(
                city = "Shanghai",
                provinceOrMunicipality = "Shanghai Municipality",
                policyTitle = "Direct Employment for Outstanding Foreign Master's Graduates (Shanghai Specific)",
                policyCategory = "Foreign Graduate Direct Employment",
                eligibility = "Foreign students graduating with a Master's degree or higher from designated Chinese universities ('Double First-Class') or world top 500 universities.",
                requirements = "Employment offer from a company registered in Shanghai; salary meeting local foreign talent standards; matching professional major; clean criminal record.",
                requiredDocuments = "Degree Certificate, Graduation Certificate, Academic Transcript, Signed Labor Contract, Legalized Non-Criminal Record, Foreigner Physical Examination Report.",
                applicationAuthority = "Shanghai Administration of Foreign Experts Affairs (上海市外国专家局) / Municipal S&T Commission",
                officialSourceUrl = "http://stcsm.sh.gov.cn",
                sourceOrganization = "Shanghai Municipal Science and Technology Commission",
                publicationDate = "2020-04-10",
                lastVerifiedDate = "Oct 2026",
                verificationStatus = "Verified recently",
                isLocalSpecific = true,
                importantUncertaintyWarning = "Shanghai-specific policy. Does NOT apply in Beijing, Hebei, or other provinces. The employer must be qualified to hire foreign personnel."
            ),
            CityImmigrationPolicyEntity(
                city = "Beijing",
                provinceOrMunicipality = "Beijing Municipality",
                policyTitle = "Zhongguancun International Graduate Innovation & Direct Employment (Beijing Specific)",
                policyCategory = "Foreign Graduate Direct Employment",
                eligibility = "Foreign graduates with Master's degrees from accredited Chinese universities joining certified high-tech enterprises in the Zhongguancun National Innovation Demonstration Zone.",
                requirements = "Employment with Zhongguancun registered enterprise; STEM or strategic emerging major; clean legal record.",
                requiredDocuments = "Degree certificate, employment contract, enterprise high-tech qualification certificate, police registration, physical exam.",
                applicationAuthority = "Beijing Municipal Science & Technology Commission & Beijing Exit-Entry Bureau",
                officialSourceUrl = "http://kw.beijing.gov.cn",
                sourceOrganization = "Beijing Municipal Science and Technology Commission",
                publicationDate = "2019-12-01",
                lastVerifiedDate = "Oct 2026",
                verificationStatus = "Verified recently",
                isLocalSpecific = true,
                importantUncertaintyWarning = "Restricted to enterprises registered inside the Zhongguancun Science Park zones. Standard Beijing municipal employment outside Zhongguancun may still require 2 years experience or 60+ points."
            ),
            CityImmigrationPolicyEntity(
                city = "Qinhuangdao",
                provinceOrMunicipality = "Hebei Province",
                policyTitle = "Student Residence Administration & Post-Graduation Transition in Qinhuangdao (Local Specific)",
                policyCategory = "Student Status & Registration",
                eligibility = "International students enrolled at Yanshan University or other higher education institutions in Qinhuangdao.",
                requirements = "Active enrollment in good standing, university enrollment certification, local accommodation registration within 24 hours.",
                requiredDocuments = "Passport, JW202/201 form, YSU school verification letter, physical examination record, local police accommodation registration.",
                applicationAuthority = "Qinhuangdao Municipal Public Security Bureau Exit-Entry Administration (秦皇岛市公安局出入境管理支队)",
                officialSourceUrl = "http://gat.hebei.gov.cn",
                sourceOrganization = "Qinhuangdao Public Security Bureau Exit-Entry Administration",
                publicationDate = "2021-08-15",
                lastVerifiedDate = "Oct 2026",
                verificationStatus = "Verified recently",
                isLocalSpecific = true,
                importantUncertaintyWarning = "Applies specifically within Qinhuangdao municipality. Work permit sponsorship for employment in Qinhuangdao follows Hebei provincial SAFEA guidelines."
            ),
            CityImmigrationPolicyEntity(
                city = "National",
                provinceOrMunicipality = "All Mainland Provinces",
                policyTitle = "Foreigner Work Permit Classification Points Matrix (National Policy)",
                policyCategory = "Work Permit Class B",
                eligibility = "Foreign personnel seeking formal employment across mainland China, evaluated under Category A (High-end, >=85 pts), Category B (Professional, >=60 pts), or Category C.",
                requirements = "Employer must be registered on SAFEA online portal. Points scored on salary, education, experience, Chinese language (HSK), age, and regional development encouragement.",
                requiredDocuments = "Notification Letter of Foreigner's Work Permit, authenticated degree, non-criminal record check, physical examination report, labor contract.",
                applicationAuthority = "Ministry of Human Resources and Social Security (MOHRSS) & SAFEA",
                officialSourceUrl = "http://www.mohrss.gov.cn",
                sourceOrganization = "Ministry of Human Resources and Social Security",
                publicationDate = "2017-03-28",
                lastVerifiedDate = "Oct 2026",
                verificationStatus = "Verified recently",
                isLocalSpecific = false,
                importantUncertaintyWarning = "Standard national policy normally requires a Bachelor's degree plus 2 years of relevant post-graduation work experience, unless a specific local foreign graduate direct employment waiver applies."
            ),
            CityImmigrationPolicyEntity(
                city = "Shenzhen",
                provinceOrMunicipality = "Guangdong Province",
                policyTitle = "Shenzhen High-Tech Foreign Graduate Employment & Talent Visa (Shenzhen Specific)",
                policyCategory = "Foreign Graduate Direct Employment",
                eligibility = "Foreign graduates with bachelor's or master's degrees from accredited Chinese universities hired by high-tech enterprises or unicorn firms in Shenzhen.",
                requirements = "Employment with Shenzhen registered high-tech or scientific research organization; STEM major alignment.",
                requiredDocuments = "Degree certificate, employment contract, enterprise tax registration, physical exam report, police registration.",
                applicationAuthority = "Shenzhen Municipal Science, Technology and Innovation Commission & Shenzhen Exit-Entry Bureau",
                officialSourceUrl = "http://stic.sz.gov.cn",
                sourceOrganization = "Shenzhen Municipal Science, Technology and Innovation Commission",
                publicationDate = "2022-01-15",
                lastVerifiedDate = "Oct 2026",
                verificationStatus = "Verified recently",
                isLocalSpecific = true,
                importantUncertaintyWarning = "Applies within Shenzhen Municipality. Check employer eligibility on Shenzhen Foreign Experts Work Permit System."
            )
        )
        db.immigrationDao().insertCityPolicies(cityPolicies)

        // 8.4 Graduation Transition Checklist
        val graduationChecklist = listOf(
            GraduationChecklistItemEntity(
                userEmail = email,
                category = "Academic",
                title = "Defend Master's Thesis & Submit Final Revisions",
                description = "Complete oral defense with faculty committee at YSU School of Information Science and submit bound thesis archive.",
                responsibleParty = "Student & Advisor",
                deadline = "May 2027",
                isCompleted = false,
                isCustom = false
            ),
            GraduationChecklistItemEntity(
                userEmail = email,
                category = "University",
                title = "Obtain Graduation Certificate & Master's Degree Certificate",
                description = "Receive formal Chinese and English degree credentials from Yanshan University Graduate School.",
                responsibleParty = "University & Student",
                deadline = "June 2027",
                isCompleted = false,
                isCustom = false
            ),
            GraduationChecklistItemEntity(
                userEmail = email,
                category = "University",
                title = "Complete University Clearance Form (离校手续单)",
                description = "Complete sign-offs across library, lab inventory return, dorm checkout, and College of International Exchange.",
                responsibleParty = "Student",
                deadline = "June 2027",
                isCompleted = false,
                isCustom = false
            ),
            GraduationChecklistItemEntity(
                userEmail = email,
                category = "Residence status",
                title = "Verify Study Residence Permit Expiration vs. Departure/Job Timeline",
                description = "Confirm current residence permit expiration date (2027-07-15). Ensure status adjustment or renewal is filed >=30 days before expiry.",
                responsibleParty = "Student & Exit-Entry Bureau",
                deadline = "June 15, 2027",
                isCompleted = false,
                isCustom = false
            ),
            GraduationChecklistItemEntity(
                userEmail = email,
                category = "Documents",
                title = "Obtain Clean Criminal Record Certificate (无犯罪记录证明)",
                description = "Secure updated certificate from home country embassy or local police station for work permit submission.",
                responsibleParty = "Student & Consular Authority",
                deadline = "May 2027",
                isCompleted = false,
                isCustom = false
            ),
            GraduationChecklistItemEntity(
                userEmail = email,
                category = "Documents",
                title = "Complete Foreigner Physical Examination Report (外国人体格检查记录)",
                description = "Schedule medical checkup at designated International Travel Healthcare Center (valid 6 months).",
                responsibleParty = "Student & Healthcare Center",
                deadline = "May 2027",
                isCompleted = false,
                isCustom = false
            ),
            GraduationChecklistItemEntity(
                userEmail = email,
                category = "Employment",
                title = "Sign Tripartite Agreement / Formal Employment Contract with Qualified Employer",
                description = "Ensure employer is registered on the SAFEA Foreigner Work Permit Service System and authorized to hire foreign staff.",
                responsibleParty = "Employer & Student",
                deadline = "April 2027",
                isCompleted = false,
                isCustom = false
            ),
            GraduationChecklistItemEntity(
                userEmail = email,
                category = "Employment",
                title = "Employer Submits Work Permit Notification Letter Application",
                description = "Employer uploads degree, contract, health check, and non-criminal record to municipal Science & Technology Bureau.",
                responsibleParty = "Employer",
                deadline = "June 2027",
                isCompleted = false,
                isCustom = false
            ),
            GraduationChecklistItemEntity(
                userEmail = email,
                category = "Residence status",
                title = "Apply for Work-Type Residence Permit (工作类居留许可)",
                description = "Upon issuance of Foreigner Work Permit Card, submit passport and permit to Municipal Exit-Entry Bureau for residence status conversion.",
                responsibleParty = "Student & Exit-Entry Bureau",
                deadline = "July 2027",
                isCompleted = false,
                isCustom = false
            )
        )
        db.immigrationDao().insertChecklistItems(graduationChecklist)

        // 8.5 Immigration Document Organizer
        val immigrationDocuments = listOf(
            ImmigrationDocumentEntity(
                userEmail = email,
                documentName = "Valid National Passport",
                requiredFor = "All immigration, residence permit, and work permit procedures",
                status = "Obtained / Ready",
                expirationDate = "2029-04-20",
                sourceRequirement = "Mandatory for all foreign nationals; must possess >6 months validity.",
                responsibleParty = "Student",
                notes = "Current validity extends to April 2029. Clean and valid."
            ),
            ImmigrationDocumentEntity(
                userEmail = email,
                documentName = "Study Residence Permit (学习类居留许可)",
                requiredFor = "Legal student residence in China and in-country work permit conversion",
                status = "Obtained / Ready",
                expirationDate = "2027-07-15",
                sourceRequirement = "Mandatory for continuous legal residence in China.",
                responsibleParty = "Student & Exit-Entry Bureau",
                notes = "Expires July 15, 2027. Renewal/transition must begin by June 15, 2027."
            ),
            ImmigrationDocumentEntity(
                userEmail = email,
                documentName = "Accommodation Registration Form (境外人员住宿登记表)",
                requiredFor = "Residence permit renewals, police compliance, and work authorization",
                status = "Obtained / Ready",
                expirationDate = "2027-07-15",
                sourceRequirement = "Required within 24 hours of moving into off-campus apartment or hotel.",
                responsibleParty = "Student & Local Police Station",
                notes = "Issued by Haigang District Police Station, Qinhuangdao."
            ),
            ImmigrationDocumentEntity(
                userEmail = email,
                documentName = "Master's Degree Certificate & Graduation Certificate",
                requiredFor = "Foreigner Work Permit (Direct Graduate Exemption / Category B)",
                status = "In Progress",
                expirationDate = "Upon Graduation (June 2027)",
                sourceRequirement = "Required by SAFEA / S&T Commission. Must be issued by accredited university.",
                responsibleParty = "Yanshan University Graduate School",
                notes = "Conferred upon successful thesis defense."
            ),
            ImmigrationDocumentEntity(
                userEmail = email,
                documentName = "Foreigner Physical Examination Record (外国人体格检查记录)",
                requiredFor = "Foreigner Work Permit application",
                status = "Not Obtained",
                expirationDate = "Valid for 6 months once issued",
                sourceRequirement = "Must be conducted by designated International Travel Healthcare Center.",
                responsibleParty = "Student & Travel Healthcare Center",
                notes = "Schedule appointment in May 2027 prior to work permit submission."
            ),
            ImmigrationDocumentEntity(
                userEmail = email,
                documentName = "Certificate of No Criminal Conviction (无犯罪记录证明)",
                requiredFor = "Foreigner Work Permit application",
                status = "In Progress",
                expirationDate = "Valid for 6 months once authenticated",
                sourceRequirement = "Must be issued by official police agency and legalized/apostilled.",
                responsibleParty = "Student & Consular Authority",
                notes = "Verify whether destination city accepts China in-country police certificate or home country check."
            ),
            ImmigrationDocumentEntity(
                userEmail = email,
                documentName = "Signed Employment Contract / Offer Letter",
                requiredFor = "Foreigner Work Permit Notification Letter",
                status = "Not Obtained",
                expirationDate = "Minimum 1-year contract duration",
                sourceRequirement = "Required by SAFEA / Labor Bureau; employer must be registered.",
                responsibleParty = "Employer & Student",
                notes = "Targeting AI Algorithm Engineer role in Shanghai."
            )
        )
        db.immigrationDao().insertDocuments(immigrationDocuments)

        // 8.6 Official Contact Directory
        val officialContacts = listOf(
            OfficialContactEntity(
                organization = "National Immigration Administration 12367 Service Platform (国家移民管理局12367服务平台)",
                location = "National (China-wide)",
                category = "National Immigration Administration",
                officialWebsite = "https://www.nia.gov.cn",
                officialPhoneOrEmail = "12367 (Domestic) / +86-10-12367 (International)",
                purpose = "24/7 bilingual hotline for entry-exit policies, visa queries, residence permit rules, and immigration services.",
                lastVerifiedDate = "Oct 2026"
            ),
            OfficialContactEntity(
                organization = "Qinhuangdao Public Security Bureau Exit-Entry Administration (秦皇岛市公安局出入境管理支队)",
                location = "Qinhuangdao, Hebei Province",
                category = "Local Exit-Entry Administration",
                officialWebsite = "http://gat.hebei.gov.cn",
                officialPhoneOrEmail = "0335-3032541 / 12367",
                purpose = "Jurisdictional exit-entry authority for Yanshan University international students: residence permit renewals, address changes, and visas.",
                lastVerifiedDate = "Oct 2026"
            ),
            OfficialContactEntity(
                organization = "Yanshan University College of International Exchange (燕山大学国际教育学院)",
                location = "Qinhuangdao, Hebei Province",
                category = "University International Student Office",
                officialWebsite = "http://sie.ysu.edu.cn",
                officialPhoneOrEmail = "0335-8071016 / sie@ysu.edu.cn",
                purpose = "International student affairs, JW202/201 form processing, university enrollment certificates, and graduation departure clearance.",
                lastVerifiedDate = "Oct 2026"
            ),
            OfficialContactEntity(
                organization = "Shanghai Administration of Foreign Experts Affairs (上海市外国专家局 / 研发公共服务平台)",
                location = "Shanghai Municipality",
                category = "Human Resources & Work Permit Authority",
                officialWebsite = "http://stcsm.sh.gov.cn",
                officialPhoneOrEmail = "021-12345 / 021-800123",
                purpose = "Foreigner Work Permit application, verification, direct master's graduate exemption, and talent classification in Shanghai.",
                lastVerifiedDate = "Oct 2026"
            ),
            OfficialContactEntity(
                organization = "Beijing Municipal Public Security Bureau Exit-Entry Administration (北京市公安局出入境管理局)",
                location = "Beijing Municipality",
                category = "Local Exit-Entry Administration",
                officialWebsite = "http://gaj.beijing.gov.cn/crj",
                officialPhoneOrEmail = "010-12367",
                purpose = "Beijing exit-entry administration, Zhongguancun international talent visas, and residence permit services.",
                lastVerifiedDate = "Oct 2026"
            )
        )
        db.immigrationDao().insertOfficialContacts(officialContacts)

        // 8.7 Personal Immigration Reminders
        val immigrationReminders = listOf(
            ImmigrationReminderEntity(
                userEmail = email,
                reminderType = "Residence Permit Expiration",
                title = "Study Residence Permit Expiration Notice",
                dueDate = "2027-06-15",
                isDismissed = false,
                label = "Personal reminder: File for residence permit extension or work transition at least 30 days before July 15, 2027."
            ),
            ImmigrationReminderEntity(
                userEmail = email,
                reminderType = "Passport Validity Check",
                title = "Passport Validity Audit",
                dueDate = "2028-10-20",
                isDismissed = false,
                label = "Personal reminder: Audit passport validity 6 months before expiration."
            ),
            ImmigrationReminderEntity(
                userEmail = email,
                reminderType = "Policy Re-check",
                title = "Shanghai Foreign Graduate Employment Policy Annual Audit",
                dueDate = "2027-03-01",
                isDismissed = false,
                label = "Personal reminder: Verify Shanghai S&T Commission updated policy criteria before job application submissions."
            )
        )
        db.immigrationDao().insertReminders(immigrationReminders)
    }
}


