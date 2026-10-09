package com.example.ai

import com.example.data.model.CourseEntity
import com.example.data.model.SkillItemEntity
import com.example.data.model.UserProfileEntity

data class LearningRecommendation(
    val id: String,
    val title: String,
    val category: String,
    val whyRecommended: String,
    val priority: String, // "High", "Medium", "Low"
    val prerequisites: String,
    val suggestedAction: String,
    val estimatedDurationWeeks: Int,
    val skillToAdd: SkillItemEntity? = null
)

object AcademicLearningAdvisor {

    fun generateCourseAndSkillRecommendations(
        profile: UserProfileEntity?,
        courses: List<CourseEntity>,
        skills: List<SkillItemEntity>,
        researchInterests: String,
        targetHskLevel: String
    ): List<LearningRecommendation> {
        val recommendations = mutableListOf<LearningRecommendation>()

        val userSkills = skills.map { it.name.lowercase() }
        val courseNames = courses.map { it.name.lowercase() }
        val interests = (researchInterests + " " + (profile?.researchInterests ?: "")).lowercase()
        val careerGoal = (profile?.careerGoals ?: "").lowercase()

        // 1. Computer Vision & Deep Learning Gap
        val hasPyTorch = userSkills.any { it.contains("pytorch") || it.contains("deep learning") }
        val hasCuda = userSkills.any { it.contains("cuda") || it.contains("gpu") }
        val isVisionFocused = interests.contains("vision") || interests.contains("point cloud") || interests.contains("3d") || interests.contains("image")

        if (isVisionFocused && !hasCuda) {
            recommendations.add(
                LearningRecommendation(
                    id = "rec_cuda_acceleration",
                    title = "CUDA C++ Parallel Programming & GPU Optimization",
                    category = "Systems & AI",
                    whyRecommended = "Your research profile targets 3D point cloud perception, which demands custom kernel optimization to reduce latency and memory overhead on GPUs.",
                    priority = "High",
                    prerequisites = "Modern C++ (C++17/20), Computer Architecture",
                    suggestedAction = "Implement custom point-cloud nearest-neighbor reduction in CUDA.",
                    estimatedDurationWeeks = 6,
                    skillToAdd = SkillItemEntity(
                        name = "CUDA & TensorRT Optimization",
                        category = "Systems",
                        level = "Advanced",
                        description = "Custom GPU CUDA kernel authoring, shared memory tiling, and TensorRT engine serialization.",
                        isCompleted = false
                    )
                )
            )
        }

        // 2. Distributed Training / Systems Gap
        val hasDistributed = userSkills.any { it.contains("distributed") || it.contains("mpi") || it.contains("ray") }
        if (!hasDistributed && (interests.contains("ai") || interests.contains("large language") || interests.contains("vision"))) {
            recommendations.add(
                LearningRecommendation(
                    id = "rec_distributed_ai",
                    title = "Distributed Systems & Large-Scale Model Parallelism",
                    category = "Systems & AI",
                    whyRecommended = "Modern foundation and perception models require multi-GPU data and tensor parallelism (DeepSpeed / Megatron-LM) during training.",
                    priority = "High",
                    prerequisites = "PyTorch, Operating Systems Internals",
                    suggestedAction = "Deploy a multi-GPU FSDP (Fully Sharded Data Parallel) benchmark on lab clusters.",
                    estimatedDurationWeeks = 4,
                    skillToAdd = SkillItemEntity(
                        name = "Distributed Model Parallelism",
                        category = "AI",
                        level = "Advanced",
                        description = "FSDP, ZeRO stage 1-3 memory partitioning, and multi-node NCCL communications.",
                        isCompleted = false
                    )
                )
            )
        }

        // 3. Technical Academic Chinese (HSK Bridge)
        val hasTechnicalChinese = userSkills.any { it.contains("chinese") || it.contains("technical terminology") }
        if (!hasTechnicalChinese || targetHskLevel.contains("5") || targetHskLevel.contains("6")) {
            recommendations.add(
                LearningRecommendation(
                    id = "rec_technical_chinese",
                    title = "Academic & Technical Chinese for CS Research",
                    category = "Chinese Language",
                    whyRecommended = "Your target profile specifies $targetHskLevel. Fluency in technical terms (特征提取, 梯度反向传播, 居留许可) is critical for lab meetings and supervisor communication.",
                    priority = "Medium",
                    prerequisites = "HSK 3 Basic Vocabulary",
                    suggestedAction = "Practice weekly lab scenario dialogues in Chinese Language Coach.",
                    estimatedDurationWeeks = 8,
                    skillToAdd = SkillItemEntity(
                        name = "Technical Chinese Fluency",
                        category = "Research Skills",
                        level = "Intermediate",
                        description = "Command of 300+ university and computer science technical Chinese terms.",
                        isCompleted = false
                    )
                )
            )
        }

        // 4. Research Reproducibility & Open Source Artifacts
        val hasDocker = userSkills.any { it.contains("docker") || it.contains("container") }
        if (!hasDocker) {
            recommendations.add(
                LearningRecommendation(
                    id = "rec_reproducible_research",
                    title = "Research Artifact Reproducibility & Docker Environments",
                    category = "Research Skills",
                    whyRecommended = "Top conferences (CVPR, ICCV, NeurIPS) strongly evaluate reproducibility checklists and deterministic artifact releases.",
                    priority = "Medium",
                    prerequisites = "Linux CLI, Git / GitHub",
                    suggestedAction = "Containerize your current research baseline with explicit seed locks and PyTorch dependencies.",
                    estimatedDurationWeeks = 3,
                    skillToAdd = SkillItemEntity(
                        name = "Reproducible Research Containers",
                        category = "Research Skills",
                        level = "Intermediate",
                        description = "Dockerized experimental workflows, WandB logging, and deterministic seed locking.",
                        isCompleted = false
                    )
                )
            )
        }

        // 5. Operating Systems Internals Course Bridge
        val hasOSCourse = courseNames.any { it.contains("operating system") || it.contains("os") }
        val hasOSKernel = userSkills.any { it.contains("kernel") || it.contains("xv6") || it.contains("posix") }
        if (!hasOSKernel) {
            recommendations.add(
                LearningRecommendation(
                    id = "rec_os_internals",
                    title = "Operating Systems Internals & Memory Virtualization",
                    category = "Core CS",
                    whyRecommended = "Deep systems understanding provides a competitive advantage in top-tier R&D engineering roles and high-throughput server architecture.",
                    priority = "Low",
                    prerequisites = "C/C++, Computer Organization",
                    suggestedAction = "Build a copy-on-write page allocator or virtual memory mapper in xv6.",
                    estimatedDurationWeeks = 5,
                    skillToAdd = SkillItemEntity(
                        name = "OS Kernel & Memory Management",
                        category = "Core CS",
                        level = "Advanced",
                        description = "Virtual memory, TLB mechanics, page replacement algorithms, and multithreaded synchronization.",
                        isCompleted = false
                    )
                )
            )
        }

        return recommendations
    }
}
