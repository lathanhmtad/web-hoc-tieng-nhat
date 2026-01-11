<template>
  <DetailPage 
    type="grammar" 
    :item="grammarData" 
    :itemId="grammarId" 
    mainField="structure"
    :notFoundMessage="t('grammarDetail.notFound')"
  >
    <template #content>
      <div v-if="isLoading" class="loading-container">
        <div class="spinner"></div>
      </div>

      <div v-else-if="grammarData" class="grammar-detail-wrapper">
        
        <section class="grammar-hero">
          <div class="badges-row">
            <span class="badge jlpt" v-if="grammarData.jlptLevel">
              {{ grammarData.jlptLevel }}
            </span>
            <span class="badge type" v-if="grammarData.grammarType">
              {{ getGrammarTypeName(grammarData.grammarType) }}
            </span>
          </div>

          <h1 class="main-title">{{ grammarData.structure }}</h1>

          <div class="meaning-box">
            <p>{{ grammarData.explanation }}</p>
          </div>
        </section>

        <section class="formation-card" v-if="grammarData.example">
          <div class="card-label">
            <span class="icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"></path><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"></path></svg>
            </span>
            <h3>Cấu trúc & Cách dùng</h3>
          </div>
          <div class="formation-content">
            <code>{{ grammarData.example }}</code>
          </div>
        </section>

        <section v-if="grammarData.sentencesLinks && grammarData.sentencesLinks.length > 0" class="examples-section">
          <div class="section-title-row">
            <h3>Ví dụ minh họa</h3>
            <span class="count">{{ grammarData.sentencesLinks.length }} mẫu câu</span>
          </div>

          <div class="example-list">
            <div v-for="(sentence, index) in grammarData.sentencesLinks" :key="index" class="example-item">
              <div class="ex-index">{{ index + 1 }}</div>
              <div class="ex-content">
                <div class="jp-text">
                  <button class="btn-speak" @click="speak(sentence.japaneseText)">🔊</button>
                  {{ sentence.japaneseText }}
                </div>
                <div class="vn-text">{{ sentence.meaning }}</div>
              </div>
            </div>
          </div>
        </section>

      </div>
    </template>
  </DetailPage>
</template>

<script>
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue'
import grammarApi from '@/api/modules/grammarApi'
import { useAudio } from '@/composables/useAudio';

export default {
  name: 'GrammarDetail',
  components: {
    DetailPage
  },
  setup() {
    const { t } = useI18n();
    const route = useRoute();
    const grammarId = ref(route.params.id);
    const grammarData = ref(null);
    const isLoading = ref(false);
    const { speak } = useAudio();

    const fetchGrammarData = async (id) => {
      try {
        isLoading.value = true;
        const response = await grammarApi.getById(id);
        grammarData.value = response;
      } catch (error) {
        console.error('Error fetching grammar data:', error);
        grammarData.value = null;
      } finally {
        isLoading.value = false;
      }
    };

    const getGrammarTypeName = (type) => {
      const types = {
        negative: 'Phủ định',
        positive: 'Khẳng định',
        progressive: 'Tiến hành',
        request: 'Yêu cầu',
        sequence: 'Trình tự',
        condition: 'Điều kiện',
        question: 'Nghi vấn',
        contrast: 'Tương phản',
        cause: 'Nguyên nhân', // Bổ sung thêm vài loại thường gặp
        purpose: 'Mục đích'
      }
      return types[type] || type
    }

    onMounted(() => {
      if (grammarId.value) {
        fetchGrammarData(grammarId.value);
      }
    });

    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          grammarId.value = newId;
          fetchGrammarData(grammarId.value);
        }
      }
    );

    return {
      t,
      grammarData,
      grammarId,
      isLoading,
      getGrammarTypeName,
      speak
    };
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/search-japanese/grammar-detail/GrammarDetail.scss';
</style>