<template>
    <div>
        <MiddlePost v-for="post in allPosts"
                    :post="post" :users="users" :comments="ourComment(post)" :key="post.id"/>
    </div>
</template>

<script>
    import MiddlePost from "./MiddlePost";
    export default {
        name: "Index",
        components: {MiddlePost},
        props: ['users', 'posts', 'comments'],
        computed: {
            allPosts: function() {
                return Object.values(this.posts).sort((p1, p2) => p2.id - p1.id);
            }
        },
        methods: {
            ourComment: function(post) {
                return Object.values(this.comments).filter(comment => comment.postId ===post.id);
            }
        }
    }
</script>

<style scoped>

</style>
