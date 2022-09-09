<template>
  <div :id = "object.name" v-if = "sortedObject.nFound > 0" class="object">
    <h3>{{ sortedObject.name }}</h3>

    <!-- fields -->
    <ul v-if="sortedObject.fields.length > 0">
      <li
        v-for="f in sortedObject.fields"
        :key="f.name"
        v-on:click="$emit('onSelectMethod', { object: object, field: f, type: 'field' })">
          {{ f.name }}
      </li>
    </ul>

    <!-- methods -->
    <ul v-if="sortedObject.methods.length > 0">
      <li
        v-for="m in sortedObject.methods"
        v-bind:class="{
          todo: m.status === 'TODO',
          todo_example: m.status === 'TODO_EXAMPLE',
          toreview: m.status === 'TOREVIEW',
          advanced: m.advanced === true,
          missing: m.status === 'missing',
        }"
        :key="m.name"
        v-on:click="$emit('onSelectMethod', { object: object, method: m, type: 'method' })">
          {{ m.name }}()
      </li>
    </ul>
  </div>
</template>

<script>
import _ from 'lodash'

export default {
  name: 'DocumentationObject',
  props: {
    object: Object,
    search: String
  },
  computed: {
    sortedObject () {
      let doc = {}
      doc.name = this.object.name

      // sorting
      doc.methods = _.sortBy(
        this.object.methods,
        'name'
      )

      doc.fields = _.sortBy(
        this.object.fields,
        'name'
      )

      // filtering
      doc.methods = doc.methods.filter(o => {
        if (doc.name.toLowerCase().includes(this.search.toLowerCase())) return o
        else if (o.name.toLowerCase().indexOf(this.search.toLowerCase()) !== -1) {
          return o
        }
      })

      doc.fields = doc.fields.filter(o => {
        if (doc.name.toLowerCase().includes(this.search.toLowerCase())) return o
        else if (o.name.toLowerCase().indexOf(this.search.toLowerCase()) !== -1) {
          return o
        }
      })

      doc.nFound = doc.methods.length + doc.fields.length

      return doc
    },
    queriedDocumentation: function () {
      var doc = _.cloneDeep(this.documentation)

      if (!doc) return
      return doc
    }
  }
}
</script>