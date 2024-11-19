import { createApp } from 'vue'
import App from './App.vue'
import PrimeVue from 'primevue/config'
import Button from 'primevue/button'
import Card from 'primevue/card'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
import ToastService from 'primevue/toastservice'
import Tooltip from 'primevue/tooltip'

const app = createApp(App)

app.use(PrimeVue, { ripple: true })
app.use(ToastService)

// eslint-disable-next-line vue/multi-word-component-names
app.component('PrimeButton', Button)
app.component('PrimeCard', Card)
app.component('PrimeDialog', Dialog)
app.component('InputText', InputText)
app.component('PrimeTextarea', Textarea)
app.directive('tooltip', Tooltip)
app.mount('#app')