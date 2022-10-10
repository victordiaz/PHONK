<template>
  <div id="nav_tabs" v-if="sharedState.preferences['editor']['tab bar']">
		<button
			id="project_run"
			v-on:click="run"
			v-bind:class="{
				torun: sharedState.device_properties.connected,
				shortcut: runShortcut,
				device_disabled: !sharedState.device_properties.connected,
			}"
		>
			<i class="material-icons" v-if = "isRunning">stop</i>
			<i class="material-icons" v-else>play_arrow</i>
		</button>
		<button id="project_save" class="icon" :class="{ device_disabled: !sharedState.device_properties.connected }" v-on:click="save">
			<i class="material-icons">save</i>
		</button>

		<ul id="tabs">
			<li
				v-bind:class="{
					active: currentTab == index,
					isModified: t.modified,
				}"
				v-on:click.prevent.self="$parent.select_tab(index)"
				v-for="(t, index) in tabs"
				:key="index"
			>
				{{ t.name }}
				<button class="close" v-on:click="$parent.close_tab(index)">
					<i class="material-icons"
						:class="{ hideClose: tabs.length === 1 || t.name === 'main.js' }"
					>close</i>
				</button>
			</li>
		</ul>

		<div v-if="false" class="visual-editor-switcher">
			<div class="visual-editor-switcher__selection" :class="{selected: !sharedState.show_ui_editor}"></div>
			<button
				v-on:click="$parent.showUIEditor(true)"
				class="visual-editor-switcher__left"
				:class="{selected: sharedState.show_ui_editor}">
				Visual
			</button>
			<button
				v-on:click="$parent.showUIEditor(false)"
				class="visual-editor-switcher__right"
				:class="{selected: !sharedState.show_ui_editor}">
				Code
			</button>
		</div>
		
	</div>
</template>

<script>
import store from '../../Store'

export default {
  name: 'EditorBar',
  props: ['tabs', 'currentTab'],
  data () {
    return {
			sharedState: store.state,
			isRunning: false,
			runShortcut: false,
			saveShortcut: false
    }
  },
	watch: {
		'sharedState.device_properties': {
			handler () {
				this.isRunning = this.sharedState.device_properties.info.script['running script'] !== 'none'
			}, deep: true
		}
	},
	created () {
    store.on('project_editor_save', this.save_feedback)
  },
  destroyed () {
    store.removeListener('project_editor_save', this.save_feedback)
  },
  methods: {
		run: function () {
      // stop the project
      if (this.isRunning) {
        store.emit('project_action', '/stop')
        this.isRunning = false
      // run the project
      } else {
        store.emit('project_action', '/stop_all_and_run')
        this.isRunning = true
      }

      // higlight run button
      this.runShortcut = true
      setTimeout(() => {
        this.runShortcut = false
      }, 200)
    },
    save: function () {
			this.$parent.project_save()
    },
		save_feedback: function () {
			this.saveShortcut = true
			setTimeout(() => {
				this.saveShortcut = false
			}, 200)
		}
  }
}
</script>

<style lang = "less">
@import (reference) '../../assets/css/variables.less';
@import (reference) '../../assets/css/hacks.less';

#nav_tabs {
	display: flex;
	flex-flow: row nowrap;
	border-bottom: 0px;
	padding-left: 0;
	margin-bottom: 0;
	align-items: center;

	#project_run {
	}

	button {
		background: transparent;
		color: var(--color-icon);
		font-size: 1em;

		&:active {
			color: var(--color-accent);
		}

		&:hover {
		}

		&.torun {
			color: var(--color-accent);
		}

		&.tostop {
		}

		&.device_disabled {
			opacity: 0.3;
			cursor: not-allowed;
			pointer-events: none;

			&:hover,
			&:active {
				background: none !important;
			}
		}
	}

	#tabs {
		flex: 2;
		list-style: none;
		color: var(--color-text-light);
		font-size: 0.8em;
		user-select: none;
		overflow-y: hidden;
		overflow-x: auto;
		white-space: nowrap;
		margin-bottom: 12px;

		li {
			position: relative;
			display: inline-block;
			padding: 18px 20px;
			cursor: pointer;
			.all-transitions;
			border-bottom: 3px solid var(--color-transparent);
			text-overflow: ellipsis;
			overflow: hidden;
			max-width: 200px;
			white-space: nowrap;
			height: 100%;
			box-sizing: border-box;

			&.active {
				border-bottom: 2px solid var(--color-text-light);
			}

			&:hover {
				.close {
					display: block;
				}
			}

			a {
				margin-left: 15px;
				font-size: 0.8em;
				margin-bottom: 0px;
				border-radius: 2px 2px 0px 0px;
				border: 0px;
			}

			&.isModified {
				border-color: var(--color-accent);
				font-style: italic;
			}

			.close {
				padding: 1px;
				display: none;
				position: absolute;
				top: 5px;
				right: 2px;

				i {
					font-size: 0.8em;
				}

				&:hover {
					color: var(--color-accent);
				}
			}

			.hideClose {
				display: none !important;
			}
		}
	}

	#add_tab {
		cursor: pointer;
		padding: 1.3em;
	}

	#add_tab:hover {
	}

	#add_tab:active {
	}


	.visual-editor-switcher {
		background: rgb(39, 40, 34) none repeat scroll 0% 0%;
		border-radius: 100px;
		height: fit-content;
		overflow: hidden;
		position: relative;
		border: 1px solid rgb(51, 52, 47);
		margin-right: 12px;

		button {
			color: rgb(93, 94, 89);
			font-size: 12px;
			padding: 2px 8px;
		}

		.visual-editor-switcher__left {
			padding-left: 10px;
		}
		
		.visual-editor-switcher__right {
			padding-right: 10px;
		}

		.selected {
			color: white;
		}
	}

	.visual-editor-switcher__selection {
		background: var(--color-main) none repeat scroll 0% 0%;
		height: 100%;
		width: calc(50% + 5px);
		position: absolute;
		.all-transitions;

		&.selected {
			transform: translate3d(100%, 0px, 0px);
		}
	}
}
</style>
