<template>
  <div>

  </div>
</template>
<script>
import { setToken } from '@/utils/auth'
import axios from 'axios'

export default {
  name: 'SuccessLogin',
  created() {
    const loginKey = this.$route.query.loginKey
    if (!loginKey) {
      alert('Login error')
      return
    }

    axios.get(this.getServerUrl() + `/system/oauth/token/get?loginKey=${loginKey}`)
      .then(response => {
        const result = response.data
        if (result.code !== '0') {
          alert('Login error')
          return
        }
        setToken(result.data)
        this.$router.push('/dashboard')
      })
  }
}
</script>

