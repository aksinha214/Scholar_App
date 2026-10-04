package com.example.ai

import com.example.data.model.GeneratedProjectEntity
import com.example.data.model.UserProfileEntity

data class ProjectSpecification(
    val title: String,
    val category: String,
    val difficulty: String,
    val researchValue: String,
    val prerequisites: String,
    val dataset: String,
    val technologies: String,
    val architecture: String,
    val milestones: String,
    val githubStructure: String,
    val readmeMarkdown: String,
    val experimentsPlan: String,
    val presentationOutline: String,
    val cvDescription: String
)

object ProjectBuilderEngine {

    fun generateProject(userPrompt: String, profile: UserProfileEntity?): ProjectSpecification {
        val p = userPrompt.lowercase()

        return if (p.contains("vision") || p.contains("image") || p.contains("point") || p.contains("lidar")) {
            ProjectSpecification(
                title = "PointFog-SAM: Zero-Shot 3D LiDAR & Multi-View Camera Fusion for Adverse Maritime Perception",
                category = "Computer Vision & Autonomous Sensing",
                difficulty = "Advanced / Research-Ready",
                researchValue = "Solves catastrophic sensor degradation in coastal maritime fog. Bridges 2D foundational segmentation models (Segment Anything Model) with sparse 3D point cloud backscatter representations for cross-modal consistency.",
                prerequisites = "PyTorch 2.x, Open3D, MinkowskiEngine, CUDA C++ programming, Differential Geometry, PointNet++ architectures.",
                dataset = "Qinhuangdao Port LiDAR Maritime Dataset (12,000 frames) + SemanticKITTI + nuScenes-Foggy benchmark.",
                technologies = "Python, PyTorch, CUDA, TensorRT, ROS2 Humble, Docker, Weights & Biases.",
                architecture = "Dual-Stream Pipeline:\n1. 3D Sparse Voxel Stream: PointNeXt backbone with depth-aware scattering compensation.\n2. 2D Foundation Stream: MobileSAM for high-confidence boundary extraction.\n3. Cross-Attention Fusion: Deformable cross-attention module aligning LiDAR depth with visual RGB tokens.\n4. Consistency Regularization Loss: Penalizes temporal flicker across sequential LiDAR sweeps under transient fog noise.",
                milestones = "Week 1: Data ingestion pipeline and synthetic fog attenuation calibration.\nWeek 2: Baseline PointNeXt reproduction and clean-vs-fog benchmark evaluation.\nWeek 3: Implementation of depth-dependent scattering inversion module in PyTorch.\nWeek 4: Cross-modal feature distillation from 2D SAM to 3D sparse voxels.\nWeek 5: Comprehensive ablation study across 4 baseline models.\nWeek 6: Packaging into open-source GitHub repo, Docker container, and CVPR manuscript draft.",
                githubStructure = """
                pointfog-sam/
                ├── configs/
                │   ├── default_fog_kitti.yaml
                │   └── qinhuangdao_port.yaml
                ├── data/
                │   ├── dataset_loader.py
                │   └── fog_simulation.py
                ├── models/
                │   ├── pointnext_backbone.py
                │   ├── cross_attention_fusion.py
                │   └── loss_functions.py
                ├── scripts/
                │   ├── train.py
                │   ├── evaluate.py
                │   └── export_tensorrt.py
                ├── docker/
                │   └── Dockerfile
                ├── README.md
                └── requirements.txt
                """.trimIndent(),
                readmeMarkdown = """
                # PointFog-SAM 🌫️🚗
                Official implementation of *Zero-Shot 3D LiDAR & Multi-View Camera Fusion for Adverse Maritime Perception*.
                
                ## Key Results
                - **+8.4% mIoU** improvement over PointNeXt under dense coastal fog conditions.
                - Real-time inference at **38 FPS** on NVIDIA Jetson AGX Orin.
                
                ## Quick Start
                ```bash
                git clone https://github.com/alexei-ysu-cs/PointFog-SAM.git
                cd PointFog-SAM
                pip install -r requirements.txt
                python scripts/train.py --config configs/qinhuangdao_port.yaml
                ```
                """.trimIndent(),
                experimentsPlan = "Ablation Matrix:\n1. Full Architecture vs Without Physics-based Scattering Filter\n2. Cross-Attention Fusion vs Simple Concatenation\n3. Temporal Consistency Loss vs Cross-Entropy only\n4. Latency vs Batch Size benchmarking on RTX 4090 and Jetson Orin.",
                presentationOutline = "Slide 1: Problem Definition & Maritime Adverse Weather Challenges\nSlide 2: Failure Analysis of Existing SOTA (PointNeXt, SphereFormer)\nSlide 3: Proposed Architecture & Mathematical Formulation\nSlide 4: Experimental Benchmark & Quantitative Results (+8.4% mIoU)\nSlide 5: Qualitative Visual Comparisons in Dense Fog\nSlide 6: Conclusion & Future Directions.",
                cvDescription = "• Engineered PointFog-SAM, a dual-stream 3D LiDAR/RGB fusion model achieving +8.4% mIoU under dense maritime coastal fog compared to SOTA PointNeXt.\n• Designed physics-based scattering inversion layers and deformable cross-attention modules deployed in real-time at 38 FPS on NVIDIA Jetson AGX Orin."
            )
        } else {
            ProjectSpecification(
                title = "EdgeGuard-AI: Distributed Real-Time TinyML Inference for Industrial Structural Defect Detection",
                category = "Edge AI & Systems Engineering",
                difficulty = "Intermediate-Advanced",
                researchValue = "Addresses high computational overhead of industrial defect detection by implementing 8-bit post-training quantization and pruned YOLO architectures running on resource-constrained embedded microcontrollers.",
                prerequisites = "C++17, PyTorch, ONNX Runtime, CMake, Linux system programming, OpenCV.",
                dataset = "NEU Surface Defect Database + High-Speed Railway Fastener Inspection Dataset (15,000 images).",
                technologies = "C++, PyTorch, ONNX, TensorRT, Raspberry Pi / STM32, CMake, GitHub Actions CI/CD.",
                architecture = "1. Model Training: Knowledge distillation from heavy ViT teacher to pruned YOLOv10-Nano student.\n2. Model Quantization: INT8 calibration using KL-divergence metric.\n3. Runtime Engine: C++ zero-copy shared memory buffer with multi-threaded camera frame grabbing.\n4. Defect Classifier: Sub-millimeter bounding box regression with confidence filtering.",
                milestones = "Week 1: Benchmark baseline detection models on industrial fastener dataset.\nWeek 2: Implement structured channel pruning and knowledge distillation.\nWeek 3: Quantize model to INT8 via TensorRT/ONNX Runtime.\nWeek 4: Write high-performance C++ inference wrapper with OpenCV.\nWeek 5: Measure thermal and latency profiling on embedded edge boards.\nWeek 6: Prepare technical documentation and demonstration video.",
                githubStructure = """
                edgeguard-ai/
                ├── cmake/
                ├── include/
                │   ├── detector.hpp
                │   └── preprocessor.hpp
                ├── src/
                │   ├── detector.cpp
                │   └── main.cpp
                ├── python_training/
                │   ├── train_distill.py
                │   └── quantize_onnx.py
                ├── benchmarks/
                └── README.md
                """.trimIndent(),
                readmeMarkdown = """
                # EdgeGuard-AI ⚡
                Low-latency, INT8 quantized industrial inspection engine for edge devices.
                
                ## Features
                - **9.2 ms inference time** per 640x640 frame on embedded CPU.
                - Sub-millimeter localization accuracy on surface micro-fractures.
                """.trimIndent(),
                experimentsPlan = "1. Teacher vs Student accuracy & recall\n2. FP32 vs FP16 vs INT8 quantization loss\n3. Throughput (FPS) across varying hardware backends (x86 vs ARM64)",
                presentationOutline = "1. Industrial Automation Bottlenecks\n2. Pruning & Distillation Strategy\n3. Zero-Copy C++ Engine Design\n4. Benchmark Results & Hardware Metrics\n5. Live Hardware Demo.",
                cvDescription = "• Developed EdgeGuard-AI in C++17 and ONNX Runtime, achieving sub-10ms defect detection on embedded ARM devices through structured pruning and INT8 quantization.\n• Automated CI/CD benchmarking pipeline verifying model accuracy against industrial quality control standards."
            )
        }
    }

    fun toEntity(spec: ProjectSpecification): GeneratedProjectEntity {
        return GeneratedProjectEntity(
            title = spec.title,
            category = spec.category,
            difficulty = spec.difficulty,
            researchValue = spec.researchValue,
            prerequisites = spec.prerequisites,
            dataset = spec.dataset,
            technologies = spec.technologies,
            architecture = spec.architecture,
            milestones = spec.milestones,
            githubStructure = spec.githubStructure,
            experimentsPlan = spec.experimentsPlan,
            cvDescription = spec.cvDescription
        )
    }
}
