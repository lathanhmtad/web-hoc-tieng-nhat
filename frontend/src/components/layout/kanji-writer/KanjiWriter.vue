<template>
    <div class="kanji-container">
        <!-- Vùng hiển thị chữ viết -->
        <div :id="elementId" class="writer-target"></div>

        <!-- Các nút điều khiển -->
        <div class="controls">
            <button @click="animate">Xem viết mẫu</button>
            <button @click="quiz">Tập viết (Quiz)</button>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import HanziWriter from 'hanzi-writer';

const props = defineProps({
    character: {
        type: String,
        required: true
    },
    size: {
        type: Number,
        default: 200
    }
});

const elementId = `kanji-writer-${Math.random().toString(36).substr(2, 9)}`;
let writerInstance = null;

const initWriter = () => {
    // Xóa nội dung cũ nếu có
    const target = document.getElementById(elementId);
    if (target) target.innerHTML = '';

    writerInstance = HanziWriter.create(elementId, props.character, {
        width: props.size,
        height: props.size,
        padding: 5,
        showOutline: true, // Hiển thị nét mờ
        strokeAnimationSpeed: 1, // Tốc độ viết
        delayBetweenStrokes: 200, // Thời gian nghỉ giữa các nét
        radicalsColor: '#337ab7', // Màu bộ thủ (nếu có dữ liệu)
    });
};

const animate = () => {
    if (writerInstance) {
        writerInstance.animateCharacter();
    }
};

const quiz = () => {
    if (writerInstance) {
        writerInstance.quiz({
            onMistake: function (strokeData) {
                console.log('Viết sai nét!');
            },
            onCorrectStroke: function (strokeData) {
                console.log('Đúng nét: ' + strokeData.strokeNum);
            },
            onComplete: function (summaryData) {
                alert('Chúc mừng! Bạn đã viết đúng.');
            }
        });
    }
};

onMounted(() => {
    initWriter();
});

// Watch nếu props thay đổi (ví dụ chuyển từ chữ 'Ngữ' sang chữ khác)
watch(() => props.character, () => {
    initWriter();
});
</script>

<style scoped>
.kanji-container {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.writer-target {
    border: 1px solid #ddd;
    background-color: #fff;
    /* Giống style thẻ bài */
    border-radius: 8px;
    margin-bottom: 10px;
}

button {
    margin: 0 5px;
    padding: 8px 16px;
    background-color: #42b983;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}
</style>


