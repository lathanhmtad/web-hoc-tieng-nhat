import { ref } from 'vue';

export function useAudio() {
    const isPlaying = ref(false);

    const speak = (text) => {
        // Kiểm tra trình duyệt có hỗ trợ không
        if (!('speechSynthesis' in window)) {
            alert('Trình duyệt của bạn không hỗ trợ phát âm thanh.');
            return;
        }

        // Dừng âm thanh đang phát (nếu có)
        window.speechSynthesis.cancel();

        const utterance = new SpeechSynthesisUtterance(text);

        // Cấu hình giọng đọc
        utterance.lang = 'ja-JP'; // Tiếng Nhật
        utterance.rate = 0.8;     // Tốc độ 0.8 (Hơi chậm một chút cho dễ nghe)
        utterance.pitch = 1;      // Cao độ bình thường
        utterance.volume = 1;     // Âm lượng tối đa

        // Sự kiện khi bắt đầu và kết thúc (để làm hiệu ứng nút bấm)
        utterance.onstart = () => {
            isPlaying.value = true;
        };
        utterance.onend = () => {
            isPlaying.value = false;
        };
        utterance.onerror = () => {
            isPlaying.value = false;
        };

        window.speechSynthesis.speak(utterance);
    };

    return {
        speak,
        isPlaying
    };
}