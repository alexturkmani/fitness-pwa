import type { Metadata } from 'next';
import SeoLanding from '../components/SeoLanding';
import { seoPageMetadata } from '../site-config';

const page = {
  path: '/free-workout-tracker-app',
  title: 'Free Workout Tracker & Gym Log App for Android',
  description: 'Log sets, reps and weight for free, review workout history and weekly volume, and keep a streak in Nexal for Android. No ads. AI plans are optional.',
};

export const metadata: Metadata = seoPageMetadata(page.path, page.title, page.description);

export default function Page() {
  return <SeoLanding
    page={page}
    slug="free_workout_tracker"
    kicker="LOG THE WORK. SEE THE TREND."
    title="Free workout tracker and gym log app for Android"
    intro="Record exercises, sets, reps and weight, use the rest timer between sets, and look back on your workout history and weekly training volume. Workout logging in Nexal is free with no ads. AI workout plans are an optional Premium feature."
    sectionTitle="A gym log that does the adding up"
    guideTopic="WORKOUT PLANNING"
    preview={{
      mode: 'sets',
      label: 'TODAY: UPPER BODY',
      heading: 'Tap a set when it is done.',
      streakDays: 12,
      initiallyDone: [0],
      sets: [
        { badge: 'S1', type: 'BENCH PRESS', name: 'Set 1', kg: 60, reps: 8 },
        { badge: 'S2', type: 'BENCH PRESS', name: 'Set 2', kg: 60, reps: 8 },
        { badge: 'S3', type: 'SEATED ROW', name: 'Set 1', kg: 50, reps: 10 },
      ],
    }}
    benefits={[
      { title: 'Custom workouts, free', text: 'Build a workout from your own exercises and log every set with reps and weight. No subscription is needed to record what you did.' },
      { title: 'History and weekly volume', text: 'Look back at past sessions and see total volume (weight × reps) for each of the last eight weeks, so progress is visible without a spreadsheet.' },
      { title: 'A streak for real days', text: 'Your streak grows on consecutive days you log something. A rest day still counts if you log a meal or water, so recovery does not cost you the streak.' },
    ]}
    feature={{
      heading: 'Why a gym log beats notes and spreadsheets',
      intro: 'Notes apps and spreadsheets are free and flexible. They are also slow to fill in between sets, and the totals only exist if you build the formulas yourself.',
      points: [
        { title: 'Structured sets, not free text', text: 'Each set is an exercise, a weight and a number of reps. Structured entries are quicker mid-session and easier to compare next week.' },
        { title: 'Totals worked out for you', text: 'Nexal adds up weekly training volume and keeps your history in order, so you can compare like with like without maintaining formulas.' },
        { title: 'Built for the bench', text: 'A rest timer, session notes and duration live in the same screen you log in, so the phone stays useful between sets.' },
      ],
    }}
    sections={[
      { title: 'Keep the spreadsheet if it works', text: 'If you enjoy building your own sheets, there is no need to switch. An app is most useful when typing into cells mid-workout is the reason you stop logging.' },
      { title: 'What the volume chart shows', text: 'Weekly volume is the sum of weight × reps. It is a useful trend for similar training, but it does not measure technique, effort or recovery, and it changes when you swap exercises.' },
      { title: 'Streaks without the guilt', text: 'A streak should reward consistency, not punish rest. Logging food or water counts as activity, so a planned recovery day keeps the streak going.' },
      { title: 'When to add AI planning', text: 'Nexal Premium can generate a four to six week gym or home plan around your goal, experience and available days. Logging stays free whether or not you use it.' },
    ]}
    faqs={[
      { q: 'Is the Nexal workout tracker free?', a: 'Yes. Creating custom workouts, logging sets, reps and weight, using the rest timer and viewing your history and weekly volume are free with no time limit.' },
      { q: 'Can I create my own workouts and exercises?', a: 'Yes. Name a workout, add your own exercises and log each set with reps and weight. You do not need an AI plan to use the tracker.' },
      { q: 'How does the Nexal streak work?', a: 'The streak grows on each consecutive day you log activity, including a workout, cardio, a meal, water or a completed fast. Missing a day restarts it.' },
      { q: 'Does Nexal sync with a smartwatch or Health Connect?', a: 'Not currently. Workouts are logged in the Nexal app on your Android phone.' },
      { q: 'What does Premium add for training?', a: 'Premium adds AI workout plans of four to six weeks with exercises, sets, reps and rest times, plus AI meal planning. Workout logging remains free.' },
    ]}
  />;
}
