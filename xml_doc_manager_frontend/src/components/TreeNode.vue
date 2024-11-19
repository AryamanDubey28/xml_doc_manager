<template>
    <div class="tree-node" :style="{ paddingLeft: `${level * 20}px` }">
      <div 
        v-if="isObject"
        class="node-content"
        @click="toggleExpanded"
      >
        <i class="pi" :class="expanded ? 'pi-chevron-down' : 'pi-chevron-right'" />
        <span class="node-label">{{ label }}</span>
      </div>
      
      <div v-else class="leaf-content">
        <span class="node-label">{{ label }}:</span>
        <span class="node-value">{{ node }}</span>
      </div>
  
      <div v-if="isObject && expanded" class="node-children">
        <TreeNode
          v-for="(value, key) in node"
          :key="key"
          :node="value"
          :label="key"
          :level="level + 1"
        />
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed } from 'vue';
  
  const props = defineProps({
    node: {
      type: [Object, Array, String, Number, Boolean],
      required: true
    },
    label: {
      type: String,
      default: ''
    },
    level: {
      type: Number,
      default: 0
    }
  });
  
  const expanded = ref(true);
  const isObject = computed(() => 
    typeof props.node === 'object' && props.node !== null
  );
  
  const toggleExpanded = () => {
    expanded.value = !expanded.value;
  };
  </script>
  
  <style scoped>
  .tree-node {
    margin: 4px 0;
  }
  
  .node-content, .leaf-content {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px;
    border-radius: 4px;
  }
  
  .node-content:hover {
    background-color: var(--surface-hover);
  }
  
  .node-label {
    font-weight: bold;
    color: var(--primary-color);
  }
  
  .node-value {
    color: var(--text-color);
  }
  
  .node-children {
    margin-left: 8px;
  }
  </style>