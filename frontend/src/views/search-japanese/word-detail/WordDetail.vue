<template>
  <DetailPage type="word" :item="wordData" :itemId="wordId" :notFoundMessage="t('wordDetail.notFound')">
    <template #content>
      <div v-if="isLoading" class="loading-container">
        <div class="spinner"></div>
      </div>

      <div v-else-if="wordData" class="word-detail-container">

        <div class="layout-grid">

          <aside class="col-kanji">
            <div class="section-label">
              <span class="icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                  stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M12 19l7-7 3 3-7 7-3-3z"></path>
                  <path d="M18 13l-1.5-7.5L2 2l3.5 14.5L13 18l5-5z"></path>
                  <path d="M2 2l7.586 7.586"></path>
                  <circle cx="11" cy="11" r="2"></circle>
                </svg>
              </span>
              <h3>Hán tự & Cách viết</h3>
            </div>
            <div class="sticky-wrapper">
              <div v-if="wordData.kanjiLinks && wordData.kanjiLinks.length > 0" class="kanji-list">
                <div v-for="(kanji, index) in wordData.kanjiLinks" :key="index" class="kanji-card">
                  <div class="kanji-display">
                    <KanjiWriter v-if="kanji.kiTu" :character="kanji.kiTu" :size="160" />
                  </div>
                  <span class="k-char">{{ kanji.label || 'Hán tự' }}</span>
                </div>
              </div>

              <div v-else class="empty-kanji">
                <p>Từ này không có liên kết Hán tự.</p>
              </div>
            </div>
          </aside>

          <main class="col-content">

            <section class="hero-card">
              <div class="word-header">
                <span class="word-type-badge">{{ getWordTypeLabel(wordData.wordType) }}</span>
                <h1 class="main-word">
                  <button class="btn-speak" @click.stop.prevent="speak(wordData.words)">🔊</button>
                  {{ wordData.words }}
                </h1>
              </div>

              <div class="readings-box">
                <div v-if="wordData.vietnamese_pronunciation" class="pronunciation-vn">
                  <span class="label">Phiên âm:</span>
                  <span class="value">/ {{ wordData.vietnamese_pronunciation }} /</span>
                </div>

                <div v-if="wordData.readings && wordData.readings.length > 0" class="readings-jp">
                  <span v-for="(reading, index) in wordData.readings" :key="index" class="reading-pill">
                    {{ reading }}
                  </span>
                </div>
              </div>

              <div class="divider"></div>

              <div class="meaning-box">
                <p>{{ wordData.meaning }}</p>
              </div>
            </section>

            <section v-if="wordData.listSampleSentences && wordData.listSampleSentences.length > 0"
              class="sentences-section">
              <div class="section-label">
                <span class="icon book-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                    <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
                  </svg>
                </span>
                <h3>Mẫu câu ví dụ</h3>
              </div>

              <div class="sentence-list">
                <div v-for="(sentence, index) in wordData.listSampleSentences" :key="index" class="sentence-item">
                  <div class="s-index">{{ index + 1 }}</div>
                  <div class="s-content">
                    <div class="jp-text">
                      <button class="btn-speak" @click="speak(sentence.japaneseText)">🔊</button>
                      {{ sentence.japaneseText }}
                    </div>
                    <div class="vn-text">{{ sentence.vietnameseMeaning }}</div>
                  </div>
                </div>
              </div>
            </section>
          </main>

        </div>
      </div>
    </template>
  </DetailPage>
</template>

<script>
// Giữ nguyên phần script của bạn
import { ref, watch, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue';
import vocabularyApi from '@/api/modules/vocabularyApi';
import KanjiWriter from '@/components/layout/kanji-writer/KanjiWriter.vue';
import { useAudio } from '@/composables/useAudio';

export default {
  name: 'WordDetail',
  components: {
    DetailPage,
    KanjiWriter
  },
  setup() {
    const { t } = useI18n();
    const route = useRoute();
    const wordId = ref(route.params.id);
    const wordData = ref(null);
    const isLoading = ref(false);
    const { speak } = useAudio()

    const getWordTypeLabel = (type) => {
      const types = {
        noun: 'Danh từ',
        verb: 'Động từ',
        adjective: 'Tính từ',
        adverb: 'Trạng từ',
        particle: 'Trợ từ',
        conjunction: 'Liên từ',
        interjection: 'Thán từ'
      }
      return types[type] || type
    }

    const fetchWordData = async (id) => {
      if (!id) return;
      try {
        isLoading.value = true;
        const response = await vocabularyApi.getById(id);
        wordData.value = response;
      } catch (error) {
        console.error('Error fetching word data:', error);
        wordData.value = null;
      } finally {
        isLoading.value = false;
      }
    };

    onMounted(() => {
      fetchWordData(wordId.value);
    });

    watch(
      () => route.params.id,
      (newId) => {
        wordId.value = newId;
        fetchWordData(newId);
      }
    );

    return {
      t,
      wordData,
      wordId,
      isLoading,
      getWordTypeLabel,
      speak
    };
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/search-japanese/word-detail/WordDetail.scss';
</style>