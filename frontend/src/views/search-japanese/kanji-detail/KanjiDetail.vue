<template>
  <DetailPage type="kanji" :item="kanjiData" :itemId="kanjiId" :notFoundMessage="t('kanjiDetail.notFound')"
    @relatedItemClick="navigateToKanjiDetail">
    <template #content>
      <div v-if="isLoading" class="loading-container">
        <div class="spinner"></div>
      </div>

      <div v-else-if="kanjiData" class="kanji-detail-container">

        <div class="layout-grid">

          <aside class="col-visual">
            <div class="sticky-wrapper">

              <div class="writer-card">
                <div class="card-header">
                  <span class="icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                      stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M12 19l7-7 3 3-7 7-3-3z"></path>
                      <path d="M18 13l-1.5-7.5L2 2l3.5 14.5L13 18l5-5z"></path>
                      <path d="M2 2l7.586 7.586"></path>
                      <circle cx="11" cy="11" r="2"></circle>
                    </svg>
                  </span>
                  <h3>Cách viết</h3>
                </div>
                <div class="writer-display">
                  <KanjiWriter v-if="kanjiData.characterName" :character="kanjiData.characterName" :size="200" />
                </div>
              </div>

              <div class="stats-grid">
                <div class="stat-item">
                  <span class="label">Số nét</span>
                  <span class="value">{{ kanjiData.strokes || '?' }}</span>
                </div>
                <div class="stat-item highlight">
                  <span class="label">Cấp độ JLPT</span>
                  <span class="value">{{ kanjiData.jlptLevel || 'N/A' }}</span>
                </div>
              </div>

            </div>
          </aside>

          <main class="col-content">

            <section class="hero-card">
              <div class="main-info">
                <h1 class="kanji-char">
                  <button class="btn-speak" @click="speak(kanjiData.characterName)">🔊</button>
                  {{ kanjiData.characterName }}
                </h1>
                <div class="meta-info">
                  <h2 class="kanji-meaning">{{ kanjiData.meaning }}</h2>
                  <span v-if="kanjiData.vietnamesePronunciation" class="han-viet-tag">
                    Hán Việt: <strong>{{ kanjiData.vietnamesePronunciation }}</strong>
                  </span>
                </div>
              </div>

              <div class="divider"></div>

              <div class="readings-container">
                <div class="reading-box on-yomi">
                  <span class="r-label">Âm On</span>
                  <p class="r-value">{{ kanjiData.onYomi || 'Không có' }}</p>
                </div>
                <div class="reading-box kun-yomi">
                  <span class="r-label">Âm Kun</span>
                  <p class="r-value">{{ kanjiData.kunYomi || 'Không có' }}</p>
                </div>
              </div>
            </section>

            <section v-if="kanjiData.vocabLinks && kanjiData.vocabLinks.length > 0" class="vocab-section">
              <div class="section-label">
                <span class="icon book-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path>
                    <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path>
                  </svg>
                </span>
                <h3>Từ vựng ứng dụng</h3>
              </div>

              <div class="vocab-grid">
                <div v-for="(vocab, index) in kanjiData.vocabLinks" :key="index" class="vocab-card"
                  @click="navigateToVocabularyDetail(vocab)">
                  <div class="vocab-word">
                    <button class="btn-speak" @click.stop="speak(vocab.word)">🔊</button>
                    {{ vocab.word }}
                  </div>
                  <div class="vocab-mean">{{ vocab.meaning }}</div>
                  <div class="arrow-action">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                      stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <line x1="5" y1="12" x2="19" y2="12"></line>
                      <polyline points="12 5 19 12 12 19"></polyline>
                    </svg>
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
// Logic Script giữ nguyên, chỉ đảm bảo đúng tên hàm
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue'
import kanjiApi from '@/api/modules/kanjiApi'
import KanjiWriter from '@/components/layout/kanji-writer/KanjiWriter.vue'
import { useAudio } from '@/composables/useAudio'

export default {
  name: 'KanjiDetail',
  components: {
    DetailPage,
    KanjiWriter
  },
  setup() {
    const { t } = useI18n();
    const route = useRoute();
    const router = useRouter();
    const kanjiId = ref(route.params.id);
    const kanjiData = ref(null);
    const relatedKanji = ref([]);
    const isLoading = ref(false);
    const { speak } = useAudio();

    const fetchKanjiData = async (id) => {
      try {
        isLoading.value = true;
        const response = await kanjiApi.getById(id);
        kanjiData.value = response;
      } catch (error) {
        console.error('Error fetching kanji data:', error);
        kanjiData.value = null;
      } finally {
        isLoading.value = false;
      }
    };

    const fetchRelatedKanji = async (id) => {
      try {
        const response = await kanjiApi.getRelated(id);
        relatedKanji.value = response || [];
      } catch (error) {
        console.error('Error fetching related kanji:', error);
        relatedKanji.value = [];
      }
    };

    const navigateToKanjiDetail = (item) => {
      router.push({ name: 'KanjiDetail', params: { id: item.kanjiId || item.id } });
    };

    // Hàm điều hướng sang trang từ vựng
    const navigateToVocabularyDetail = (item) => {
      // Kiểm tra input để lấy đúng ID (tùy format API trả về)
      const id = item.vocalbId || item.id || item.wordId;
      if (id) {
        router.push({ name: 'WordDetail', params: { id: id } });
      }
    };

    onMounted(() => {
      if (kanjiId.value) {
        fetchKanjiData(kanjiId.value);
        fetchRelatedKanji(kanjiId.value);
      }
    });

    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          kanjiId.value = newId;
          fetchKanjiData(kanjiId.value);
          fetchRelatedKanji(kanjiId.value);
        }
      }
    );

    return {
      t,
      kanjiData,
      relatedKanji,
      kanjiId,
      isLoading,
      navigateToKanjiDetail,
      navigateToVocabularyDetail,
      speak
    };
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/search-japanese/kanji-detail/KanjiDetail.scss';
</style>