<template>
  <div class="camera-record">
    <div v-if="!modelValue">
      <div
        v-if="!isRecording"
        class="flex flex-col items-center justify-center p-4 border border-dashed rounded-md bg-gray-50 dark:bg-gray-800"
      >
        <video
          ref="videoRef"
          autoplay
          muted
          playsinline
          class="w-full h-auto max-h-[250px] mb-2 bg-black rounded"
        ></video>
        <el-button type="primary" :disabled="disabled" @click="startRecording">
          <Icon icon="ep:video-camera" class="mr-1" />打开摄像头并录制
        </el-button>
      </div>
      <div
        v-else
        class="flex flex-col items-center justify-center p-4 border border-dashed rounded-md border-red-500 bg-red-50 dark:bg-red-900/20"
      >
        <video
          ref="videoRef"
          autoplay
          muted
          playsinline
          class="w-full h-auto max-h-[250px] mb-2 bg-black rounded"
        ></video>
        <div class="flex items-center space-x-2 mt-2">
          <span class="text-red-500 font-bold animate-pulse">正在录制记账现场视频...</span>
          <el-button type="danger" @click="stopRecording">
            <Icon icon="ep:video-pause" class="mr-1" />停止并上传
          </el-button>
        </div>
      </div>
    </div>
    <div v-else>
      <div class="flex flex-col items-center p-4 border border-solid rounded-md border-green-500 bg-green-50 dark:bg-green-900/20">
        <video
          :src="modelValue"
          controls
          playsinline
          class="w-full h-auto max-h-[250px] mb-2 rounded"
        ></video>
        <div class="flex items-center space-x-2 mt-2">
          <span class="text-green-600 font-bold">
            <Icon icon="ep:circle-check" class="mr-1" />视频已存证
          </span>
          <el-button v-if="!disabled" type="warning" size="small" @click="clearVideo">重新录制</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount } from 'vue'
import { useMessage } from '@/hooks/web/useMessage'
import { useUpload } from '@/components/UploadFile/src/useUpload'

defineOptions({ name: 'CameraRecord' })

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])
const message = useMessage()
const { httpRequest } = useUpload() // 借用已有的云端直传/后端上传逻辑

const isRecording = ref(false)
const videoRef = ref<HTMLVideoElement | null>(null)
let mediaRecorder: MediaRecorder | null = null
let recordedChunks: Blob[] = []
let stream: MediaStream | null = null

const startRecording = async () => {
  try {
    // 拉取视音频流
    stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true })
    if (videoRef.value) {
      videoRef.value.srcObject = stream
    }
    recordedChunks = []
    
    // 强制使用常见的视频格式避免不兼容
    let options = { mimeType: 'video/webm;codecs=vp9,opus' }
    if (!MediaRecorder.isTypeSupported(options.mimeType)) {
      options = { mimeType: 'video/webm;codecs=vp8,opus' }
      if (!MediaRecorder.isTypeSupported(options.mimeType)) {
        options = { mimeType: 'video/webm' }
      }
    }

    mediaRecorder = new MediaRecorder(stream, options)
    
    mediaRecorder.ondataavailable = (event) => {
      if (event.data && event.data.size > 0) {
        recordedChunks.push(event.data)
      }
    }
    
    mediaRecorder.onstop = async () => {
      stopStream() // 关闭绿灯和流占用
      const blob = new Blob(recordedChunks, { type: mediaRecorder?.mimeType || 'video/webm' })
      if (blob.size === 0) {
        message.warning('未录制到有效视频')
        return
      }
      await uploadVideo(blob)
    }
    
    // 每 1000 毫秒收集一次数据
    mediaRecorder.start(1000)
    isRecording.value = true
  } catch (err) {
    message.error('无法打开摄像头，请确保页面在 HTTPS 下且用户授权了摄像头权限。')
    console.error(err)
  }
}

const stopRecording = () => {
  if (mediaRecorder && mediaRecorder.state !== 'inactive') {
    mediaRecorder.stop()
  }
  isRecording.value = false
}

const stopStream = () => {
  if (stream) {
    stream.getTracks().forEach((track) => track.stop())
    stream = null
  }
}

const clearVideo = () => {
  stopStream()
  emit('update:modelValue', '')
}

const uploadVideo = async (blob: Blob) => {
  try {
    message.success('正在提取视频流并上传...')
    // 将 blob 包装成 File 供 httpRequest 使用
    const file = new File([blob], `record_${Date.now()}.webm`, { type: blob.type })
    
    // 模拟 Element el-upload 的上传参数 options
    const options: any = {
      file: file,
      filename: file.name,
      action: '',
      onProgress: () => {}
    }

    const { data } = await httpRequest(options)
    if (data) {
      message.success('录像存证上传成功！')
      emit('update:modelValue', data)
    }
  } catch (err) {
    message.error('视频存证上传失败，请重试')
    console.error('上传出错:', err)
  }
}

onBeforeUnmount(() => {
  stopStream()
})
</script>

<style scoped>
.camera-record video {
  max-width: 100%;
}
</style>
