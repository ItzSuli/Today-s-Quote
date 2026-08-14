package com.itzsuli.todaysquote.data

import com.itzsuli.todaysquote.data.Category.ADVERSITY
import com.itzsuli.todaysquote.data.Category.CRAFT
import com.itzsuli.todaysquote.data.Category.DISCIPLINE
import com.itzsuli.todaysquote.data.Category.FREEDOM
import com.itzsuli.todaysquote.data.Category.MIND
import com.itzsuli.todaysquote.data.Category.MORTALITY
import com.itzsuli.todaysquote.data.Category.POWER
import com.itzsuli.todaysquote.data.Category.SOLITUDE
import com.itzsuli.todaysquote.data.Category.STRATEGY
import com.itzsuli.todaysquote.data.Category.TRUTH

/**
 * The built-in library. Curated for weight over popularity: no motivational-poster
 * filler, no quotes worn smooth by overuse. Ids are stable and must never be reused.
 */
object BuiltInQuotes {

    private var counter = 0
    private fun q(text: String, author: String, category: Category): Quote =
        Quote(id = "b:${++counter}", text = text, author = author, category = category)

    val all: List<Quote> = listOf(
        // ---------------------------------------------------------------- Discipline
        q("Waste no more time arguing what a good man should be. Be one.", "Marcus Aurelius", DISCIPLINE),
        q("First say to yourself what you would be; and then do what you have to do.", "Epictetus", DISCIPLINE),
        q("Dripping water hollows out stone, not through force but through persistence.", "Ovid", DISCIPLINE),
        q("No man is free who is not master of himself.", "Epictetus", DISCIPLINE),
        q("Rest at the end, not in the middle.", "Kobe Bryant", DISCIPLINE),
        q("Do not pray for an easy life; pray for the strength to endure a difficult one.", "Bruce Lee", DISCIPLINE),
        q("Do nothing which is of no use.", "Miyamoto Musashi", DISCIPLINE),
        q("Think lightly of yourself and deeply of the world.", "Miyamoto Musashi", DISCIPLINE),
        q("Begin at once to live, and count each separate day as a separate life.", "Seneca", DISCIPLINE),
        q("Difficulties strengthen the mind, as labour does the body.", "Seneca", DISCIPLINE),
        q("As long as you live, keep learning how to live.", "Seneca", DISCIPLINE),
        q("Never confuse movement with action.", "Ernest Hemingway", DISCIPLINE),
        q("Take a simple idea and take it seriously.", "Charlie Munger", DISCIPLINE),
        q("If you wish to be a writer, write.", "Epictetus", DISCIPLINE),
        q("Ambition means tying your well-being to what other people say or do. Sanity means tying it to your own actions.", "Marcus Aurelius", DISCIPLINE),
        q("Nothing is enough for the man to whom enough is too little.", "Epicurus", DISCIPLINE),
        q("Never let the future disturb you. You will meet it, if you have to, with the same weapons of reason which today arm you against the present.", "Marcus Aurelius", DISCIPLINE),
        q("Character is destiny.", "Heraclitus", DISCIPLINE),
        q("It is not that we have a short time to live, but that we waste much of it.", "Seneca", DISCIPLINE),
        q("No great thing is created suddenly.", "Epictetus", DISCIPLINE),

        // ----------------------------------------------------------------- Adversity
        q("The world breaks everyone, and afterward, many are strong at the broken places.", "Ernest Hemingway", ADVERSITY),
        q("What matters most is how well you walk through the fire.", "Charles Bukowski", ADVERSITY),
        q("Let everything happen to you: beauty and terror. Just keep going. No feeling is final.", "Rainer Maria Rilke", ADVERSITY),
        q("In the depth of winter, I finally learned that within me there lay an invincible summer.", "Albert Camus", ADVERSITY),
        q("We suffer more often in imagination than in reality.", "Seneca", ADVERSITY),
        q("No man is more unhappy than he who never faces adversity, for he is not permitted to prove himself.", "Seneca", ADVERSITY),
        q("Anyone can hold the helm when the sea is calm.", "Publilius Syrus", ADVERSITY),
        q("My life has been full of terrible misfortunes, most of which never happened.", "Michel de Montaigne", ADVERSITY),
        q("Scars have the strange power to remind us that our past is real.", "Cormac McCarthy", ADVERSITY),
        q("Perhaps all the dragons in our lives are princesses who are only waiting to see us act, just once, with beauty and courage.", "Rainer Maria Rilke", ADVERSITY),
        q("When we are no longer able to change a situation, we are challenged to change ourselves.", "Viktor Frankl", ADVERSITY),
        q("There is no fate that cannot be surmounted by scorn.", "Albert Camus", ADVERSITY),
        q("Be like the rocky headland on which the waves constantly break. It stands firm, and round it the seething waters are laid to rest.", "Marcus Aurelius", ADVERSITY),
        q("Everything I've ever let go of has claw marks on it.", "David Foster Wallace", ADVERSITY),
        q("Life shrinks or expands in proportion to one's courage.", "Anaïs Nin", ADVERSITY),
        q("You forget what you want to remember, and you remember what you want to forget.", "Cormac McCarthy", ADVERSITY),
        q("The good fighters of old first put themselves beyond the possibility of defeat, and then waited for an opportunity of defeating the enemy.", "Sun Tzu", ADVERSITY),
        q("The best revenge is not to be like your enemy.", "Marcus Aurelius", ADVERSITY),

        // ------------------------------------------------------------------ Solitude
        q("All of humanity's problems stem from man's inability to sit quietly in a room alone.", "Blaise Pascal", SOLITUDE),
        q("You do not need to leave your room. Just wait, be quiet, still and solitary. The world will freely offer itself to you to be unmasked.", "Franz Kafka", SOLITUDE),
        q("Some people never go crazy. What truly horrible lives they must lead.", "Charles Bukowski", SOLITUDE),
        q("The thief left it behind — the moon at the window.", "Ryokan", SOLITUDE),
        q("I am too intelligent, too demanding, and too resourceful for anyone to be able to take charge of me entirely.", "Simone de Beauvoir", SOLITUDE),
        q("Know how to withdraw. If it is a great lesson in life to know how to deny, it is a still greater to know how to deny oneself.", "Baltasar Gracián", SOLITUDE),
        q("The only journey is the one within.", "Rainer Maria Rilke", SOLITUDE),
        q("It never ceases to amaze me: we all love ourselves more than other people, but care more about their opinion than our own.", "Marcus Aurelius", SOLITUDE),
        q("A good traveller has no fixed plans and is not intent on arriving.", "Lao Tzu", SOLITUDE),
        q("You've got to learn to leave the table when love's no longer being served.", "Nina Simone", SOLITUDE),
        q("Be patient toward all that is unsolved in your heart and try to love the questions themselves.", "Rainer Maria Rilke", SOLITUDE),

        // ----------------------------------------------------------------- Mortality
        q("You could leave life right now. Let that determine what you do and say and think.", "Marcus Aurelius", MORTALITY),
        q("How we spend our days is, of course, how we spend our lives.", "Annie Dillard", MORTALITY),
        q("Do not act as if you were going to live ten thousand years. While you live, while it is in your power, be good.", "Marcus Aurelius", MORTALITY),
        q("I took a deep breath and listened to the old brag of my heart. I am, I am, I am.", "Sylvia Plath", MORTALITY),
        q("Buy the ticket, take the ride.", "Hunter S. Thompson", MORTALITY),
        q("Do not regret what you have done.", "Miyamoto Musashi", MORTALITY),
        q("Things do not change; we change.", "Henry David Thoreau", MORTALITY),
        q("It is not enough to be busy; so are the ants. The question is: what are we busy about?", "Henry David Thoreau", MORTALITY),
        q("Life can only be understood backwards; but it must be lived forwards.", "Søren Kierkegaard", MORTALITY),
        q("Accept everything just the way it is.", "Miyamoto Musashi", MORTALITY),

        // --------------------------------------------------------------------- Power
        q("The desire for safety stands against every great and noble enterprise.", "Tacitus", POWER),
        q("Knowing others is intelligence; knowing yourself is true wisdom. Mastering others is strength; mastering yourself is true power.", "Lao Tzu", POWER),
        q("Never contend with a man who has nothing to lose.", "Baltasar Gracián", POWER),
        q("Keep the extent of your abilities unknown.", "Baltasar Gracián", POWER),
        q("Do not explain overmuch.", "Baltasar Gracián", POWER),
        q("Men judge generally more by the eye than by the hand, for everyone can see and few can feel.", "Niccolò Machiavelli", POWER),
        q("Regard your soldiers as your children, and they will follow you into the deepest valleys.", "Sun Tzu", POWER),
        q("When I dare to be powerful, to use my strength in the service of my vision, then it becomes less and less important whether I am afraid.", "Audre Lorde", POWER),
        q("He who fights with monsters should look to it that he himself does not become a monster.", "Friedrich Nietzsche", POWER),
        q("Talent hits a target no one else can hit; genius hits a target no one else can see.", "Arthur Schopenhauer", POWER),
        q("Everyone thinks of changing the world, but no one thinks of changing himself.", "Leo Tolstoy", POWER),
        q("The end justifies the means. But what if there never is an end? All we have is means.", "Ursula K. Le Guin", POWER),

        // --------------------------------------------------------------------- Truth
        q("The first principle is that you must not fool yourself — and you are the easiest person to fool.", "Richard Feynman", TRUTH),
        q("Above all, don't lie to yourself.", "Fyodor Dostoevsky", TRUTH),
        q("To see what is in front of one's nose needs a constant struggle.", "George Orwell", TRUTH),
        q("The most common lie is the one a man tells to himself.", "Friedrich Nietzsche", TRUTH),
        q("Not everything that is faced can be changed, but nothing can be changed until it is faced.", "James Baldwin", TRUTH),
        q("It is impossible for a man to learn what he thinks he already knows.", "Epictetus", TRUTH),
        q("The truth will set you free. But not until it is finished with you.", "David Foster Wallace", TRUTH),
        q("A book must be the axe for the frozen sea within us.", "Franz Kafka", TRUTH),
        q("Everyone complains of his memory, and no one complains of his judgment.", "François de La Rochefoucauld", TRUTH),
        q("I'm for truth, no matter who tells it.", "Malcolm X", TRUTH),
        q("People are trapped in history, and history is trapped in them.", "James Baldwin", TRUTH),
        q("We are never as happy or as unhappy as we imagine.", "François de La Rochefoucauld", TRUTH),

        // ---------------------------------------------------------------------- Mind
        q("Until you make the unconscious conscious, it will direct your life and you will call it fate.", "Carl Jung", MIND),
        q("Where your fear is, there is your task.", "Carl Jung", MIND),
        q("Everything that irritates us about others can lead us to an understanding of ourselves.", "Carl Jung", MIND),
        q("Anxiety is the dizziness of freedom.", "Søren Kierkegaard", MIND),
        q("You must have chaos within you to give birth to a dancing star.", "Friedrich Nietzsche", MIND),
        q("A man can do what he wants, but not want what he wants.", "Arthur Schopenhauer", MIND),
        q("The two enemies of human happiness are pain and boredom.", "Arthur Schopenhauer", MIND),
        q("Attention is the rarest and purest form of generosity.", "Simone Weil", MIND),
        q("A wealth of information creates a poverty of attention.", "Herbert A. Simon", MIND),
        q("In the beginner's mind there are many possibilities, but in the expert's there are few.", "Shunryu Suzuki", MIND),
        q("To study the self is to forget the self.", "Dōgen", MIND),
        q("Man is a mystery. It must be unravelled, and if you spend your whole life unravelling it, do not say that you have wasted time.", "Fyodor Dostoevsky", MIND),
        q("The most common form of despair is not being who you are.", "Søren Kierkegaard", MIND),
        q("We are what we pretend to be, so we must be careful about what we pretend to be.", "Kurt Vonnegut", MIND),
        q("A process cannot be understood by stopping it. Understanding must move with the flow of the process.", "Frank Herbert", MIND),
        q("Love is the extremely difficult realisation that something other than oneself is real.", "Iris Murdoch", MIND),
        q("The only way to make sense out of change is to plunge into it, move with it, and join the dance.", "Alan Watts", MIND),
        q("There is always some madness in love. But there is also always some reason in madness.", "Friedrich Nietzsche", MIND),

        // --------------------------------------------------------------------- Craft
        q("Perceive that which cannot be seen with the eye.", "Miyamoto Musashi", CRAFT),
        q("Do not fear mistakes. There are none.", "Miles Davis", CRAFT),
        q("If there's a book that you want to read, but it hasn't been written yet, then you must write it.", "Toni Morrison", CRAFT),
        q("And now that you don't have to be perfect, you can be good.", "John Steinbeck", CRAFT),
        q("The snake which cannot cast its skin has to die.", "Friedrich Nietzsche", CRAFT),
        q("Genius is the ability to renew one's emotions in daily experience.", "Paul Cézanne", CRAFT),
        q("Inspiration exists, but it has to find you working.", "Pablo Picasso", CRAFT),
        q("Perfection is achieved not when there is nothing more to add, but when there is nothing left to take away.", "Antoine de Saint-Exupéry", CRAFT),
        q("Making the simple complicated is commonplace; making the complicated simple, awesomely simple, that is creativity.", "Charles Mingus", CRAFT),
        q("The best thing a human being can do is to help another human being know more.", "Charlie Munger", CRAFT),

        // ------------------------------------------------------------------- Freedom
        q("The only way to deal with an unfree world is to become so absolutely free that your very existence is an act of rebellion.", "Albert Camus", FREEDOM),
        q("Freedom is what you do with what's been done to you.", "Jean-Paul Sartre", FREEDOM),
        q("No one can construct for you the bridge upon which precisely you must cross the stream of life, no one but you yourself alone.", "Friedrich Nietzsche", FREEDOM),
        q("In the struggle between yourself and the world, second the world.", "Franz Kafka", FREEDOM),
        q("We are our choices.", "Jean-Paul Sartre", FREEDOM),
        q("Become who you are.", "Friedrich Nietzsche", FREEDOM),
        q("You cannot buy the revolution. You cannot make the revolution. You can only be the revolution.", "Ursula K. Le Guin", FREEDOM),
        q("Anything that gets your blood racing is probably worth doing.", "Hunter S. Thompson", FREEDOM),
        q("Everywhere, at each moment, you have the option to accept this event with humility.", "Marcus Aurelius", FREEDOM),
        q("The cave you fear to enter holds the treasure you seek.", "Joseph Campbell", FREEDOM),
        q("Man is the only creature who refuses to be what he is.", "Albert Camus", TRUTH),
        q("The privilege of a lifetime is being who you are.", "Joseph Campbell", FREEDOM),
        q("What is to give light must endure burning.", "Viktor Frankl", ADVERSITY),
        q("He who has a why to live can bear almost any how.", "Friedrich Nietzsche", ADVERSITY),
        q("Hell is empty and all the devils are here.", "William Shakespeare", POWER),
        q("The heart is forever inexperienced.", "Henry David Thoreau", MIND),
        q("What you do not want done to yourself, do not do to others.", "Confucius", TRUTH),
        q("Silence is a true friend who never betrays.", "Confucius", SOLITUDE),
        q("It is better to be feared than loved, if you cannot be both.", "Niccol\u00f2 Machiavelli", POWER),
        q("Time is the substance I am made of.", "Jorge Luis Borges", MORTALITY),
        // ------------------------------------------------------------------ Strategy
        // Sun Tzu is quoted from the Lionel Giles translation of The Art of War, which is
        // the standard English rendering; the popular lines absent from it are left out.
        q("All warfare is based on deception.", "Sun Tzu", STRATEGY),
        q("When we are able to attack, we must seem unable; when using our forces, we must seem inactive.", "Sun Tzu", STRATEGY),
        q("Attack him where he is unprepared; appear where you are not expected.", "Sun Tzu", STRATEGY),
        q("If you know the enemy and know yourself, you need not fear the result of a hundred battles.", "Sun Tzu", STRATEGY),
        q("Supreme excellence consists in breaking the enemy\u0027s resistance without fighting.", "Sun Tzu", STRATEGY),
        q("The clever combatant imposes his will on the enemy, but does not allow the enemy\u0027s will to be imposed on him.", "Sun Tzu", STRATEGY),
        q("Water shapes its course according to the ground; the soldier works out his victory in relation to the foe he is facing.", "Sun Tzu", STRATEGY),
        q("Let your plans be dark and impenetrable as night, and when you move, fall like a thunderbolt.", "Sun Tzu", STRATEGY),
        q("To secure ourselves against defeat lies in our own hands, but the opportunity of defeating the enemy is provided by the enemy himself.", "Sun Tzu", STRATEGY),
        q("He will win who knows when to fight and when not to fight.", "Sun Tzu", STRATEGY),
        q("There is no instance of a country having benefited from prolonged warfare.", "Sun Tzu", STRATEGY),
        q("Do not repeat the tactics which have gained you one victory, but let your methods be regulated by the infinite variety of circumstances.", "Sun Tzu", STRATEGY),
        q("Energy may be likened to the bending of a crossbow; decision, to the releasing of the trigger.", "Sun Tzu", STRATEGY),
        q("Move not unless you see an advantage; use not your troops unless there is something to be gained.", "Sun Tzu", STRATEGY),
        q("Whoever is first in the field and awaits the coming of the enemy will be fresh for the fight.", "Sun Tzu", STRATEGY),
        q("The general who advances without coveting fame and retreats without fearing disgrace is the jewel of the kingdom.", "Sun Tzu", STRATEGY),
        q("Rapidity is the essence of war.", "Sun Tzu", STRATEGY),
        q("Hold out baits to entice the enemy. Feign disorder, and crush him.", "Sun Tzu", STRATEGY),
        q("The skilful fighter puts himself into a position which makes defeat impossible.", "Sun Tzu", STRATEGY),
        q("Everything in war is very simple, but the simplest thing is difficult.", "Carl von Clausewitz", STRATEGY),
        q("War is the realm of uncertainty; three quarters of the factors on which action is based are wrapped in a fog of greater or lesser uncertainty.", "Carl von Clausewitz", STRATEGY),
        q("Let him who desires peace prepare for war.", "Vegetius", STRATEGY),
        q("The strong do what they can and the weak suffer what they must.", "Thucydides", POWER),
        q("The secret of happiness is freedom, and the secret of freedom is courage.", "Thucydides", FREEDOM),
        q("You can only fight the way you practise.", "Miyamoto Musashi", DISCIPLINE),
        q("Be detached from desire your whole life long.", "Miyamoto Musashi", DISCIPLINE),
        q("Do not act following customary beliefs.", "Miyamoto Musashi", FREEDOM),
        q("You must understand that there is more than one path to the top of the mountain.", "Miyamoto Musashi", MIND),
        q("Matters of small concern should be treated seriously.", "Yamamoto Tsunetomo", DISCIPLINE),
        q("Never tell people how to do things. Tell them what to do and they will surprise you with their ingenuity.", "George S. Patton", CRAFT)
    )

    /** Fast lookup used when resolving ids stored in widget settings or favourites. */
    val byId: Map<String, Quote> = all.associateBy { it.id }
}
