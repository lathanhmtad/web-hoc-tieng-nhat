// frontend/src/composables/useAudio.js

import { ref } from 'vue';

export function useAudio() {
    const isPlaying = ref(false);

    const speak = (text) => {
        if (!('speechSynthesis' in window)) {
            alert('Trình duyệt của bạn không hỗ trợ phát âm thanh.');
            return;
        }

        // Hủy bỏ âm thanh đang đọc (nếu có)
        window.speechSynthesis.cancel();

        const utterance = new SpeechSynthesisUtterance(text);
        utterance.lang = 'ja-JP';
        utterance.rate = 0.8;
        utterance.pitch = 1;
        utterance.volume = 1;

        utterance.onstart = () => { isPlaying.value = true; };
        utterance.onend = () => { isPlaying.value = false; };
        utterance.onerror = () => { isPlaying.value = false; };

        // FIX: Thêm setTimeout để tránh xung đột với lệnh cancel()
        setTimeout(() => {
            window.speechSynthesis.speak(utterance);
        }, 10);
    };

    return {
        speak,
        isPlaying
    };
}