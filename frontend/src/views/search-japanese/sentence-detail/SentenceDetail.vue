<template>
  <DetailPage type="sentence" :item="sentenceData" :itemId="sentenceId" :notFoundMessage="t('sentenceDetail.notFound')">
    <template #content>
      <div v-if="isLoading" class="loading-container">
        <div class="spinner"></div>
      </div>

      <div v-else-if="sentenceData" class="sentence-detail-wrapper">

        <div class="sentence-card">
          <div class="card-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
          </div>

          <h1 class="jp-text">
            <button class="btn-speak large" @click="speak(sentenceData.japaneseText)">🔊</button>
            {{ sentenceData.japaneseText }}
          </h1>

          <div class="divider"></div>

          <div class="vn-meaning">
            {{ sentenceData.vietnameseMeaning }}
          </div>
        </div>

        <div v-if="sentenceData.grammarLink" class="meta-section">
          <div class="section-label">Ngữ pháp liên quan</div>

          <div class="grammar-link-card" @click="navigateToGrammar(sentenceData.grammarLink)">
            <span class="icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="16" x2="12" y2="12"></line>
                <line x1="12" y1="8" x2="12.01" y2="8"></line>
              </svg>
            </span>
            <span class="label">{{ sentenceData.grammarLink.label || 'Xem chi tiết ngữ pháp' }}</span>
            <span class="arrow">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="5" y1="12" x2="19" y2="12"></line>
                <polyline points="12 5 19 12 12 19"></polyline>
              </svg>
            </span>
          </div>
        </div>

      </div>
    </template>
  </DetailPage>
</template>

<script>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue'
import sampleSentenceApi from '@/api/modules/sampleSentenceApi'
import { useAudio } from '@/composables/useAudio'

export default {
  name: 'SentenceDetail',
  components: {
    DetailPage
  },
  setup() {
    const { t } = useI18n()
    const route = useRoute()
    const router = useRouter()
    const sentenceId = ref(route.params.id)
    const sentenceData = ref(null)
    const isLoading = ref(false)
    const { speak } = useAudio()

    const fetchSentenceData = async (id) => {
      try {
        isLoading.value = true;
        const response = await sampleSentenceApi.getById(id);
        sentenceData.value = response;
      } catch (error) {
        console.error('Error fetching sentence data:', error);
        sentenceData.value = null;
      } finally {
        isLoading.value = false;
      }
    };

    // Hàm điều hướng sang trang ngữ pháp
    const navigateToGrammar = (grammarLink) => {
      // Giả sử grammarLink có chứa id hoặc grammarId. 
      // Bạn cần điều chỉnh key này theo đúng API trả về.
      const id = grammarLink.id || grammarLink.grammarId;
      if (id) {
        router.push({ name: 'GrammarDetail', params: { id: id } });
      }
    };

    onMounted(() => {
      if (sentenceId.value) {
        fetchSentenceData(sentenceId.value);
      }
    });

    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          sentenceId.value = newId;
          fetchSentenceData(sentenceId.value);
        }
      }
    );

    return {
      t,
      sentenceData,
      sentenceId,
      isLoading,
      navigateToGrammar,
      speak
    };
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/search-japanese/sentence-detail/SentenceDetail.scss';
</style>