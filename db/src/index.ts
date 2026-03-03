import { type BunFile } from "bun";
import camelcaseKeys from "camelcase-keys";

const token: string = Bun.env.TMDB_TOKEN as string;
const options: RequestInit = {
  method: "GET",
  headers: {
    accept: "application/json",
    Authorization: `Bearer ${token}`,
  },
};

const promises: Promise<Response>[] = [];
const totalMovies: number = 10000;
for (let i: number = 1; i <= totalMovies; i++) {
  promises.push(fetch(`https://api.themoviedb.org/3/movie/${i}`, options));
}

const results: PromiseSettledResult<Response>[] =
  await Promise.allSettled(promises);
const availableItems: any[] = [];

for (const result of results.values()) {
  if (result.status === "fulfilled") {
    const data: any = await result.value.json();
    if ("status_code" in data || "status_message" in data) {
      continue;
    }
    availableItems.push(data);
  }
}
// console.log(availableItems);
console.log(`จำนวนหนังทั้งหมด: ${availableItems.length}`);

const file: BunFile = Bun.file("./data/movie_details.json");
file.write(JSON.stringify(camelcaseKeys(availableItems), null, 2));
