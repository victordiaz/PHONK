<template>
  <div class = "calender">
    <p class = "title"> {{month}} / {{year}} </p>
    <table>
      <thead>
        <tr>
          <th v-for = "d in days">{{d}}</th>
        </tr>
      </thead>
      <tr v-for = "">
        <td v-for = ""> </td>
      </tr>
    </table>

  </div>
</template>

<script>
export default {
  name: 'Calender',
  props: {
    prop1: String,
    prop2: Number
  },
  data () {
    return {
      days: ['L', 'M', 'X', 'J', 'V', 'S', 'D'],
      months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
      year: null,
      month: '',
      day: 1
    }
  },
  computed: {
    fn: function () {
    }
  },
  methods: {
    getDaysInAMonth: function (month, year) {
      var d = new Date(year, month, 0)
      return d.getDate()
    },
    getFirstDayInAMonth: function (month, year) {
      var firstDay = new Date(year, month, 1)
      return firstDay
    }
  },
  created () {
    this.month = this.months[1]
    this.year = 2016
    console.log(this.getDaysInAMonth(7, this.year))
    console.log(this.getFirstDayInAMonth(7, this.year))
    console.log(new Date(2016, 7, 1).getDay())
  }
}
</script>

<style lang = "less" scoped>
@import "../../assets/css/variables.less";

.calender {
  border: 1px solid black;
  padding: 10px;
  width: 200px;
}

.title {
  text-align: center;
  margin-bottom: 5px;
}

table {
  width: 100%;
}

thead {
  border-bottom: 1px solid black;
}

th {
  padding: 5px;
}

</style>
