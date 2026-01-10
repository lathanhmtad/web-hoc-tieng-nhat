<template>
  <div class="create-quiz">
    <div class="header-section">
      <div class="flex-jsb">
        <h1>{{ t('createQuiz.title') }}</h1>
        <button @click="openAIModal" class="ai-btn" :disabled="isGeneratingAI">
          <i v-if="isGeneratingAI" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-magic"></i>
          {{ isGeneratingAI ? t('createQuiz.generating') : t('createQuiz.aiButton') }}
        </button>
      </div>
    </div>

    <div class="form-container">
      <div class="form-group">
        <label>{{ t('createQuiz.nameLabel') }}</label>
        <input v-model="title" type="text" :placeholder="t('createQuiz.namePlaceholder')" />
      </div>
      <div class="form-group">
        <label>{{ t('createQuiz.descriptionLabel') }}</label>
        <textarea v-model="description" :placeholder="t('createQuiz.descriptionPlaceholder')"></textarea>
      </div>
      <div class="questions-container">
        <h2>{{ t('createQuiz.questions') }}</h2>
        <div v-for="(question, qIndex) in questions" :key="qIndex" class="question-item">
          <div class="question-header">
            <span>{{ t('createQuiz.question') }} {{ qIndex + 1 }}</span>
            <button @click="removeQuestion(qIndex)" class="remove-btn">
              <i class="fas fa-trash"></i>
            </button>
          </div>
          <div class="question-content">
            <input v-model="question.text" type="text" :placeholder="t('createQuiz.questionPlaceholder')" />
            <div class="options-container">
              <div v-for="(option, oIndex) in question.options" :key="oIndex" class="option-item">
                <input type="radio" :name="'question-' + qIndex" :id="'q' + qIndex + 'o' + oIndex" :value="oIndex"
                  v-model="question.correctAnswer" />
                <input type="text" v-model="question.options[oIndex]"
                  :placeholder="t('createQuiz.option') + ' ' + (oIndex + 1)" />
                <button @click="removeOption(qIndex, oIndex)" class="remove-option-btn"
                  v-if="question.options.length > 2">
                  <i class="fas fa-times"></i>
                </button>
              </div>
              <button @click="addOption(qIndex)" class="add-option-btn" v-if="question.options.length < 4">
                <i class="fas fa-plus"></i>
                {{ t('createQuiz.addOption') }}
              </button>
            </div>
          </div>
        </div>
        <button @click="addQuestion" class="add-question-btn">
          <i class="fas fa-plus"></i>
          {{ t('createQuiz.addQuestion') }}
        </button>
      </div>
      <div class="form-actions">
        <button @click="saveQuiz" class="save-btn">{{ t('createQuiz.saveQuiz') }}</button>
      </div>
    </div>

    <!-- AI Configuration Modal -->
    <div v-if="showAIModal" class="ai-modal-overlay">
      <div class="ai-modal-content">
        <div class="ai-modal-header">
          <h3>Cấu hình tạo câu hỏi AI</h3>
          <button @click="showAIModal = false" class="close-modal-btn"><i class="fas fa-times"></i></button>
        </div>

        <div class="ai-modal-body">
          <div class="form-group">
            <label>Trình độ (Level)</label>
            <select v-model="aiConfig.level">
              <option value="N5">N5</option>
              <option value="N4">N4</option>
              <option value="N3">N3</option>
              <option value="N2">N2</option>
              <option value="N1">N1</option>
            </select>
          </div>

          <div class="form-group">
            <label>Số lượng câu hỏi: {{ aiConfig.quantity }}</label>
            <input type="range" v-model.number="aiConfig.quantity" min="5" max="25" step="5" />
          </div>

          <div class="form-group">
            <label>Chế độ hiển thị</label>
            <div class="radio-group">
              <label class="radio-item">
                <input type="radio" v-model="aiConfig.kanjiMode" value="KANJI_ONLY">
                Có Kanji
              </label>
              <label class="radio-item">
                <input type="radio" v-model="aiConfig.kanjiMode" value="NO_KANJI">
                Không Kanji
              </label>
              <label class="radio-item">
                <input type="radio" v-model="aiConfig.kanjiMode" value="MIXED">
                Vừa Kanji vừa Hiragana
              </label>
            </div>
          </div>

          <div class="form-group">
            <label>Chọn nguồn dữ liệu (Flashcards)</label>
            <div class="flashcard-list">
              <div v-if="loadingFlashcards">Đang tải dữ liệu...</div>
              <div v-else-if="userFlashcardSets.length === 0">Bạn chưa có bộ Flashcard nào.</div>
              <div v-else v-for="set in userFlashcardSets" :key="set.maBoThe" class="checkbox-item">
                <input type="checkbox" :id="'fc-' + set.maBoThe" :value="set.maBoThe" v-model="aiConfig.flashcardIds">
                <label :for="'fc-' + set.maBoThe">{{ set.tenBoThe || 'Flashcard ' + set.maBoThe }}</label>
              </div>
            </div>
          </div>
        </div>

        <div class="ai-modal-actions">
          <button @click="showAIModal = false" class="cancel-btn">Hủy</button>
          <button @click="createQuizWithAI" class="generate-btn" :disabled="aiConfig.flashcardIds.length === 0">
            <i class="fas fa-robot"></i> Tạo ngay
          </button>
        </div>
      </div>
    </div>

    <!-- Premium Required Popup -->
    <ThePopup v-if="showPremiumPopup" :title="premiumPopupTitle" :message="premiumPopupMessage"
      :confirmText="t('createQuiz.upgradePremium')" @confirm="handlePremiumPopupConfirm"
      @cancel="handlePremiumPopupCancel" :showCancel="true" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { useI18n } from 'vue-i18n'
import questionApi from '@/api/modules/questionApi'
import aiApi from '@/api/modules/aiApi'
import flashcardApi from '@/api/modules/flashcardApi'
import { usePremiumCheck } from '@/composables/usePremiumCheck'
import ThePopup from '@/components/common/popup/ThePopup.vue'

const router = useRouter()
const store = useStore()
const { t } = useI18n()

// Premium check
const {
  checkPremiumFeature,
  showPremiumPopup,
  premiumPopupMessage,
  premiumPopupTitle,
  handlePremiumPopupConfirm,
  handlePremiumPopupCancel
} = usePremiumCheck()

const title = ref('')
const description = ref('')
const questions = ref([
  {
    text: '',
    options: ['', ''],
    correctAnswer: 0
  }
])

const isGeneratingAI = ref(false)
const showAIModal = ref(false)
const loadingFlashcards = ref(false)
const userFlashcardSets = ref([])

// --- Question Management Functions ---
const addQuestion = () => {
  questions.value.push({
    text: '',
    options: ['', ''],
    correctAnswer: 0
  })
}

const removeQuestion = (qIndex) => {
  questions.value.splice(qIndex, 1)
}

const addOption = (qIndex) => {
  if (questions.value[qIndex].options.length < 4) {
    questions.value[qIndex].options.push('')
  }
}

const removeOption = (qIndex, oIndex) => {
  if (questions.value[qIndex].options.length > 2) {
    questions.value[qIndex].options.splice(oIndex, 1)
    if (questions.value[qIndex].correctAnswer >= oIndex) {
      questions.value[qIndex].correctAnswer = Math.max(0, questions.value[qIndex].correctAnswer - 1)
    }
  }
}

// --- Validation ---
const validateQuiz = () => {
  if (!title.value.trim()) {
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('createQuiz.nameRequired')
    });
    return false;
  }

  if (questions.value.length === 0) {
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('createQuiz.questionRequired')
    });
    return false;
  }

  for (let i = 0; i < questions.value.length; i++) {
    const question = questions.value[i];
    if (!question.text.trim()) {
      store.dispatch('message/showMessage', {
        type: 'error',
        text: `${t('createQuiz.questionContentRequired')} ${i + 1}.`
      });
      return false;
    }

    const emptyOption = question.options.findIndex(opt => !opt.trim());
    if (emptyOption !== -1) {
      store.dispatch('message/showMessage', {
        type: 'error',
        text: `${t('createQuiz.optionContentRequired')} ${emptyOption + 1} ${t('createQuiz.of')} ${i + 1}.`
      });
      return false;
    }
  }

  return true;
}

// --- AI Logic Implementation ---
const aiConfig = reactive({
  quantity: 10,
  kanjiMode: 'MIXED', // mixed, KANJI_ONLY, NO_KANJI
  level: 'N5',
  flashcardIds: [] // List UUID
})

const openAIModal = async () => {
  // 1. Check Premium trước
  if (!(await checkPremiumFeature())) {
    return
  }

  // 2. Load danh sách flashcard của user nếu chưa có
  if (userFlashcardSets.value.length === 0) {
    await fetchUserFlashcards()
  }

  // 3. Mở Modal
  showAIModal.value = true
}

// Hàm lấy danh sách flashcard (Mock hoặc gọi API thật)
const fetchUserFlashcards = async () => {
  try {
    loadingFlashcards.value = true
    const response = await flashcardApi.getUserFlashcards()
    userFlashcardSets.value = response.content || []
  } catch (error) {
    console.error('Failed to load flashcards', error)
  } finally {
    loadingFlashcards.value = false
  }
}

const createQuizWithAI = async () => {
  // Kiểm tra premium trước khi sử dụng tính năng AI
  if (!(await checkPremiumFeature())) {
    return
  }

  try {
    isGeneratingAI.value = true
    showAIModal.value = false // Đóng modal

    // Tạo payload gửi đi
    const payload = {
      flashcardIds: aiConfig.flashcardIds,
      quantity: aiConfig.quantity,
      kanjiMode: aiConfig.kanjiMode,
      level: aiConfig.level
    }

    // Gọi API để tạo câu hỏi ngẫu nhiên bằng AI
    const response = await aiApi.generateQuestionsByUserId(payload)

    if (!Array.isArray(response) || response.length === 0) {
      store.dispatch('message/showMessage', {
        type: 'error',
        text: t('createQuiz.aiGenerateError')
      })
      return
    }

    // Chuyển đổi dữ liệu từ API thành format phù hợp cho FlashcardTest
    const learningItems = response.map(q => ({
      type: 'question',
      id: q.questionID,
      front: q.questionName,
      back: q.correctAnswer,
      options: q.options,
      content: q.questionName,
      backcontent: q.correctAnswer
    }))

    // Lưu vào store
    await store.dispatch('flashcard/setLearningItems', learningItems)

    // Chuyển hướng đến trang test với thông tin AI-generated
    router.push({
      path: '/flashcard/test',
      query: {
        type: 'multiple-choice',
        aiGenerated: 'true',
        source: 'ai-generated',
        title: t('createQuiz.aiGeneratedTitle'),
        description: t('createQuiz.aiGeneratedDescription', { count: response.length })
      }
    })

  } catch (error) {
    console.error('Error generating AI quiz:', error)
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('createQuiz.aiError') + error.message
    })
  } finally {
    isGeneratingAI.value = false
  }
}

const saveQuiz = async () => {
  if (!validateQuiz()) return;

  const quizData = {
    title: title.value.trim(),
    description: description.value.trim()
  };

  try {
    // Bước 1: Tạo quiz
    const response = await store.dispatch('quiz/createQuiz', quizData);

    // Bước 2: Tạo câu hỏi cho quiz
    const questionsList = questions.value.map(q => ({
      questionName: q.text.trim(),
      correctAnswer: q.options[q.correctAnswer].trim(),
      options: q.options.map(opt => opt.trim()),
      quizId: response.quizzesID
    }));

    // Gọi API tạo câu hỏi
    await questionApi.create(questionsList);

    store.dispatch('message/showMessage', {
      type: 'success',
      text: t('createQuiz.createSuccess')
    });

    // Chuyển hướng đến trang thư viện
    router.push('/library');
  } catch (error) {
    console.error('Error creating quiz:', error);
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('createQuiz.saveError') + error.message
    });
  }
}
</script>

<style lang="scss" scoped>
@use '@/views/create-learn/create-quiz/CreateQuiz.scss';

.ai-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.ai-modal-content {
  background: white;
  width: 90%;
  max-width: 500px;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  animation: slideIn 0.3s ease;

  .ai-modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h3 {
      margin: 0;
      color: #333;
    }

    .close-modal-btn {
      background: none;
      border: none;
      font-size: 1.2rem;
      cursor: pointer;
      color: #999;

      &:hover {
        color: #333;
      }
    }
  }

  .ai-modal-body {
    .form-group {
      margin-bottom: 8px;

      label {
        display: block;
        font-weight: 600;
        margin-bottom: 8px;
        color: #555;
      }

      select {
        padding: 6px 32px;
      }

      .radio-group {
        display: flex;
        flex-direction: column;

        .radio-item {
          display: flex;
          align-items: center;
          gap: 8px;
        }

        input {
          width: auto;
        }
      }

      input[type="range"] {
        padding: 0;
      }

      .flashcard-list {
        max-height: 150px;
        overflow-y: auto;
        border: 1px solid #eee;
        padding: 10px;
        border-radius: 6px;

        .checkbox-item {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 4px 0;

          label {
            margin: 0;
            font-weight: normal;
            cursor: pointer;
          }

          input {
            width: auto;
          }
        }
      }
    }
  }

  .ai-modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 24px;
    border-top: 1px solid #eee;
    padding-top: 16px;

    button {
      padding: 10px 20px;
      border-radius: 8px;
      border: none;
      cursor: pointer;
      font-weight: 600;
    }

    .cancel-btn {
      background: #f5f5f5;
      color: #666;

      &:hover {
        background: #e0e0e0;
      }
    }

    .generate-btn {
      background: #D13651; // Primary color
      color: white;

      &:hover {
        background: #b02b42;
      }

      &:disabled {
        background: #ffcfd9;
        cursor: not-allowed;
      }
    }
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>