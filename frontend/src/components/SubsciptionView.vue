<template>

<v-data-table
    :headers="headers"
    :items="subsciptionView"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'SubsciptionView',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "subscriptionId", value: "subscriptionId" },
            { text: "subscriptionStatus", value: "subscriptionStatus" },
            { text: "productName", value: "productName" },
            { text: "paymentId", value: "paymentId" },
            { text: "paymentStatus", value: "paymentStatus" },
            { text: "underwritingId", value: "underwritingId" },
            { text: "underwritingStatus", value: "underwritingStatus" },
            { text: "underwriterId", value: "underwriterId" },
        ],
        subsciptionView : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/subsciptionviews')

      this.subsciptionView = temp.data._embedded.subsciptionviews;

    },
    methods: {
    }
  }
</script>

