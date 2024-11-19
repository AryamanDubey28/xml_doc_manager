<template>
    <div class="tree-viewer">
      <div v-if="data.metadata" class="mb-4">
        <div class="text-xl font-bold mb-2">{{ data.metadata.className }}</div>
        <div class="text-sm text-color-secondary">{{ data.metadata.packageName }}</div>
      </div>
      
      <div class="tree-content">
        <TreeNode 
          v-if="data.object"
          :node="data.object" 
          :level="0"
        />
      </div>
    </div>
  </template>
  
  <script setup>
  import { defineProps } from 'vue';
  import TreeNode from './TreeNode.vue';
  
  defineProps({
    data: {
      type: Object,
      required: true
    }
  });
  </script>
  
  <style scoped>
  .tree-viewer {
    font-family: monospace;
  }
  </style>