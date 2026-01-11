<template>
  <div v-if="item" class="detail-page" :class="{ 'has-back-button': showBackButton }">
    <!-- Back Button -->
    <button v-if="showBackButton" @click="goBack" class="back-button">
      <i class="fas fa-arrow-left"></i>
      {{ t('detail.backToLibrary') }}
    </button>

    <div class="detail-card">
      <!-- Action Buttons -->
      <div class="action-buttons">
        <button
          class="action-btn"
          :class="{ active: isFavorite }"
          @click="toggleFavorite"
        >
          <i class="fas fa-star"></i>
        </button>

        <button
          class="action-btn"
          :class="{ active: isInFlashcards }"
          @click="toggleFlashcard"
        >
          <i class="fas fa-book"></i>
        </button>
      </div>

      <!-- Main Content -->
      <div class="main-info">
        <slot name="content" />
      </div>

      <!-- Comments -->
      <div class="comments-section">
        <CommentSection :type="type" :itemId="itemId" />
      </div>
    </div>
  </div>

  <div v-else class="not-found">
    {{ notFoundMessageText }}
  </div>
</template>

<script>
import { defineComponent, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import CommentSection from '@/components/layout/comment/CommentSection.vue';

export default defineComponent({
  name: 'DetailPage',
  components: {
    CommentSection
  },
  props: {
    type: {
      type: String,
      required: true,
      validator: value =>
        ['word', 'kanji', 'grammar', 'sentence'].includes(value)
    },
    item: {
      type: Object,
      default: null
    },
    itemId: {
      type: [String, Number],
      required: true
    },
    notFoundMessage: {
      type: String,
      default: ''
    }
  },
  setup(props) {
    const store = useStore();
    const route = useRoute();
    const router = useRouter();
    const { t } = useI18n();

    /* ===============================
     * Navigation
     * =============================== */
    const showBackButton = computed(
      () => route.query.from === 'library'
    );

    const goBack = () => {
      router.push({
        path: '/library',
        query: {
          tab: 'favorites',
          favoriteTab: route.query.favoriteTab
        }
      });
    };

    /* ===============================
     * UI Text
     * =============================== */
    const notFoundMessageText = computed(
      () => props.notFoundMessage || t('detail.noDataFound')
    );

    /* ===============================
     * Favorite
     * =============================== */
    const isFavorite = computed(() => {
      const currentId = route.params.id;
      if (!currentId) return false;

      return store.state.user.favoriteItems.some(fav => {
        switch (props.type) {
          case 'word':
            return fav.vocabularyId === currentId;
          case 'grammar':
            return fav.grammaId === currentId; // API dùng grammaId
          case 'kanji':
            return fav.kanjiId === currentId;
          case 'sentence':
            return fav.sampleSentenceId === currentId;
          default:
            return false;
        }
      });
    });

    const toggleFavorite = () => {
      const currentId = route.params.id;
      if (!currentId) return;

      store.dispatch('user/toggleFavorite', {
        type: props.type,
        itemId: currentId
      });
    };

    /* ===============================
     * Flashcard
     * =============================== */
    const isInFlashcards = computed(() => {
      const currentId = route.params.id;
      if (!currentId) return false;

      return store.getters['flashcard/isInFlashcard'](
        props.type,
        currentId
      );
    });

    const toggleFlashcard = () => {
      const currentId = route.params.id;
      if (!currentId) return;

      const action = isInFlashcards.value
        ? 'flashcard/removeItem'
        : 'flashcard/addItem';

      store.dispatch(action, {
        type: props.type,
        id: currentId,
        ...props.item
      });
    };

    /* ===============================
     * Lifecycle
     * =============================== */
    onMounted(() => {
      store.dispatch('user/fetchFavorites');
    });

    return {
      t,
      showBackButton,
      goBack,
      notFoundMessageText,
      isFavorite,
      isInFlashcards,
      toggleFavorite,
      toggleFlashcard
    };
  }
});
</script>

<style lang="scss" scoped>
@use "@/components/layout/detail-search/DetailSearch.scss";
</style>
